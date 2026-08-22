package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.LibroDao;
import com.biblioteca.virtual.dao.PrestamoDao;
import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Libro;
import com.biblioteca.virtual.domain.Prestamo;
import com.biblioteca.virtual.domain.Usuario;
import java.time.LocalDate;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PrestamoServiceImpl implements PrestamoService {

    @Autowired
    private PrestamoDao prestamoDao;
    
    @Autowired
    private LibroDao libroDao;
    
    @Autowired
    private UsuarioDao usuarioDao;
    
    @Autowired
    private ConfiguracionService configuracionService;

    @Override
    @Transactional
    public void realizarPrestamo(Long idLibro, String username) throws Exception {
        com.biblioteca.virtual.domain.Usuario usuario = usuarioDao.findByUsername(username);
        com.biblioteca.virtual.domain.Libro libro = libroDao.findById(idLibro)
                .orElseThrow(() -> new Exception("El libro no existe en el catálogo."));

        // 1. Verificamos si ya tiene saldo pendiente cobrado por el sistema
        if (usuario.getMultaPendiente() != null && usuario.getMultaPendiente() > 0) {
            throw new Exception("Cargos por mora, por favor normalizar para poder realizar la transaccion.");
        }

        // --- TRAEMOS LAS REGLAS DE NEGOCIO DINÁMICAS DESDE LA BASE DE DATOS ---
        com.biblioteca.virtual.domain.Configuracion config = configuracionService.obtenerConfiguracion();

        java.util.List<com.biblioteca.virtual.domain.Prestamo> historialPrestamos = this.obtenerPrestamosPorUsername(username);
        java.time.LocalDate hoy = java.time.LocalDate.now();
        
        // 2. Verificamos EN TIEMPO REAL si tiene algún libro vencido hoy
        for (com.biblioteca.virtual.domain.Prestamo p : historialPrestamos) {
            if ("ACTIVO".equalsIgnoreCase(p.getEstado()) && p.getFechaDevolucion() != null && p.getFechaDevolucion().isBefore(hoy)) {
                throw new Exception("Cargos por mora, por favor normalizar para poder realizar la transaccion.");
            }
        }

        // 3. Verificamos el límite máximo de libros prestados dinámicamente
        long librosActivos = historialPrestamos.stream()
                .filter(p -> "ACTIVO".equalsIgnoreCase(p.getEstado()))
                .count();
                
        if (librosActivos >= config.getMaxLibros()) {
            throw new Exception("Has alcanzado el límite máximo de " + config.getMaxLibros() + " libros. Por favor, devuelve algún ejemplar antes de realizar nuevas reservas.");
        }

        // 4. Verificamos disponibilidad de inventario
        if (libro.getCantidad() <= 0) {
            throw new Exception("No hay ejemplares disponibles por el momento.");
        }

        // 5. ¡Todo en orden! Procesamos el préstamo
        libro.setCantidad(libro.getCantidad() - 1); // Restamos 1 al stock
        libroDao.save(libro);

        com.biblioteca.virtual.domain.Prestamo nuevoPrestamo = new com.biblioteca.virtual.domain.Prestamo();
        nuevoPrestamo.setLibro(libro);
        nuevoPrestamo.setUsuario(usuario);
        nuevoPrestamo.setFechaPrestamo(hoy);
        // Aplicamos los días de préstamo dinámicos
        nuevoPrestamo.setFechaDevolucion(hoy.plusDays(config.getDiasPrestamo())); 
        nuevoPrestamo.setEstado("ACTIVO");

        prestamoDao.save(nuevoPrestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamosPorUsername(String username) {
        Usuario usuario = usuarioDao.findByUsername(username);
        return prestamoDao.findByUsuario(usuario);
    }

    @Override
    @Transactional
    public void devolverLibro(Long idPrestamo, String username) throws Exception {
        // 1. Buscamos el recibo en la base de datos
        com.biblioteca.virtual.domain.Prestamo prestamo = prestamoDao.findById(idPrestamo)
                .orElseThrow(() -> new Exception("El recibo de préstamo no existe."));

        // 2. Seguridad: Validamos que el libro sea del estudiante correcto
        if (!prestamo.getUsuario().getUsername().equals(username)) {
            throw new Exception("No tienes permiso para devolver este ejemplar.");
        }

        // 3. Validamos que el libro realmente esté prestado
        if (!"ACTIVO".equalsIgnoreCase(prestamo.getEstado())) {
            throw new Exception("Este ejemplar ya fue entregado o se encuentra actualmente en revisión.");
        }

        // 4. NUEVO FLUJO: El libro pasa a la mesa de inspección del administrador
        prestamo.setEstado("EN REVISION");
        
        // Guardamos el cambio de estado
        prestamoDao.save(prestamo);
    }

    @Override
    public java.util.List<com.biblioteca.virtual.domain.Prestamo> getPrestamos() {
        return (java.util.List<com.biblioteca.virtual.domain.Prestamo>) prestamoDao.findAll();
    }

    @Override
    @Transactional
    public void procesarRevision(Long idPrestamo, String evaluacion) throws Exception {
        com.biblioteca.virtual.domain.Prestamo prestamo = prestamoDao.findById(idPrestamo)
                .orElseThrow(() -> new Exception("El recibo no existe."));
        
        com.biblioteca.virtual.domain.Usuario usuario = prestamo.getUsuario();
        com.biblioteca.virtual.domain.Libro libro = prestamo.getLibro();
        Double multaActual = usuario.getMultaPendiente() != null ? usuario.getMultaPendiente() : 0.0;

        switch (evaluacion) {
            case "OPTIMO":
                libro.setCantidad(libro.getCantidad() + 1); // Vuelve al estante intacto
                break;
            case "DANO_PARCIAL":
                usuario.setMultaPendiente(multaActual + 5000.0);
                libro.setCantidad(libro.getCantidad() + 1); // Vuelve al estante reparado
                break;
            case "DANO_TOTAL":
                usuario.setMultaPendiente(multaActual + 15000.0);
                // NO sumamos el libro al catálogo porque quedó destruido
                break;
            default:
                throw new Exception("Evaluación no válida");
        }

        prestamo.setEstado("DEVUELTO"); // Terminamos el ciclo
        
        usuarioDao.save(usuario);
        libroDao.save(libro);
        prestamoDao.save(prestamo);
    }
}