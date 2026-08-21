package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Prestamo;
import com.biblioteca.virtual.domain.Usuario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
public class MultaScheduler {

    @Autowired
    private PrestamoService prestamoService; // Usamos tu servicio para traer los préstamos

    @Autowired
    private UsuarioDao usuarioDao;

    // Se ejecuta automáticamente todos los días a la medianoche (00:00)
    // TIP: Para probarlo ahorita mismo, puedes comentar la línea de @Scheduled(cron...) 
    // y descomentar la de @Scheduled(fixedRate = 60000) para que corra cada 1 minuto.
    
    @Scheduled(cron = "0 0 0 * * *")
    // @Scheduled(fixedRate = 60000) 
    public void calcularMultasDiarias() {
        System.out.println("🤖 [SISTEMA] Iniciando cálculo automático de multas por mora...");

        // 1. Obtenemos todos los usuarios y enceramos sus multas para evitar cobros duplicados
        List<Usuario> todosLosUsuarios = (List<Usuario>) usuarioDao.findAll();
        for (Usuario u : todosLosUsuarios) {
            u.setMultaPendiente(0.0);
        }

        // 2. Obtenemos todos los préstamos del sistema
        List<Prestamo> todosLosPrestamos = prestamoService.getPrestamos();
        LocalDate hoy = LocalDate.now();

        // 3. Revisamos uno por uno buscando a los morosos
        for (Prestamo prestamo : todosLosPrestamos) {
            // Solo nos interesan los libros que NO han sido devueltos
            if ("ACTIVO".equalsIgnoreCase(prestamo.getEstado())) {
                
                // Extraemos la fecha límite (asumiendo que usaste LocalDate en tu modelo)
                LocalDate fechaLimite = prestamo.getFechaDevolucion(); 

                // Si la fecha límite ya pasó (es anterior a hoy)...
                if (fechaLimite != null && hoy.isAfter(fechaLimite)) {
                    
                    // Contamos cuántos días exactos han pasado
                    long diasAtraso = ChronoUnit.DAYS.between(fechaLimite, hoy);
                    
                    // Multiplicamos por la tarifa de tu biblioteca (500 colones)
                    double montoMulta = diasAtraso * 500.0;

                    // Le sumamos la multa al usuario dueño de este préstamo
                    Usuario usuario = prestamo.getUsuario();
                    if(usuario != null) {
                        usuario.setMultaPendiente(usuario.getMultaPendiente() + montoMulta);
                    }
                }
            }
        }

        // 4. Guardamos todos los usuarios actualizados de golpe en la base de datos
        usuarioDao.saveAll(todosLosUsuarios);
        System.out.println("🤖 [SISTEMA] Cálculo de multas finalizado con éxito.");
    }
}
