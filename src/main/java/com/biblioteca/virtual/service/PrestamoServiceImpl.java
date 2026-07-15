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

    @Override
    @Transactional
    public void realizarPrestamo(Long idLibro, String username) throws Exception {
        // 1. Buscamos el libro en la base de datos
        Libro libro = libroDao.findById(idLibro).orElseThrow(() -> new Exception("Libro no encontrado"));
        
        // 2. Verificamos que haya unidades disponibles
        if (libro.getCantidad() <= 0) {
            throw new Exception("No hay unidades disponibles de este libro.");
        }

        // 3. Buscamos al estudiante que tiene la sesión iniciada
        Usuario usuario = usuarioDao.findByUsername(username);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado.");
        }

        // 4. Creamos el recibo
        Prestamo prestamo = new Prestamo();
        prestamo.setLibro(libro);
        prestamo.setUsuario(usuario);
        prestamo.setFechaPrestamo(LocalDate.now());
        prestamo.setFechaDevolucion(LocalDate.now().plusDays(7)); // Tienen 7 días para devolverlo
        prestamo.setEstado("ACTIVO");

        // 5. Descontamos 1 unidad del inventario físico
        libro.setCantidad(libro.getCantidad() - 1);

        // 6. Guardamos los cambios en la base de datos
        libroDao.save(libro);
        prestamoDao.save(prestamo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Prestamo> obtenerPrestamosPorUsername(String username) {
        Usuario usuario = usuarioDao.findByUsername(username);
        return prestamoDao.findByUsuario(usuario);
    }
}
