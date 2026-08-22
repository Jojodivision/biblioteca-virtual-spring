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
    private PrestamoService prestamoService;

    @Autowired
    private UsuarioDao usuarioDao;
    
    // --- NUEVO: Inyectamos el servicio de reglas de negocio ---
    @Autowired
    private ConfiguracionService configuracionService;

    @Scheduled(cron = "0 0 0 * * *")
    // @Scheduled(fixedRate = 60000) 
    public void calcularMultasDiarias() {
        System.out.println("🤖 [SISTEMA] Iniciando cálculo automático de multas por mora...");
        
        // --- NUEVO: Obtenemos la tarifa dinámica de la base de datos ---
        com.biblioteca.virtual.domain.Configuracion config = configuracionService.obtenerConfiguracion();
        double tarifaDiaria = config.getMultaDiaria();

        // 1. Obtenemos todos los usuarios y enceramos sus multas para evitar cobros duplicados
        // (Nota: Tenemos pendiente revisar esta línea para no borrar las multas por daños)
        List<Usuario> todosLosUsuarios = (List<Usuario>) usuarioDao.findAll();
        for (Usuario u : todosLosUsuarios) {
            u.setMultaPendiente(0.0);
        }

        // 2. Obtenemos todos los préstamos del sistema
        List<Prestamo> todosLosPrestamos = prestamoService.getPrestamos();
        LocalDate hoy = LocalDate.now();

        // 3. Revisamos uno por uno buscando a los morosos
        for (Prestamo prestamo : todosLosPrestamos) {
            if ("ACTIVO".equalsIgnoreCase(prestamo.getEstado())) {
                
                LocalDate fechaLimite = prestamo.getFechaDevolucion(); 

                if (fechaLimite != null && hoy.isAfter(fechaLimite)) {
                    
                    long diasAtraso = ChronoUnit.DAYS.between(fechaLimite, hoy);
                    
                    // --- NUEVO: Multiplicamos por la tarifa dinámica ---
                    double montoMulta = diasAtraso * tarifaDiaria;

                    Usuario usuario = prestamo.getUsuario();
                    if(usuario != null) {
                        usuario.setMultaPendiente(usuario.getMultaPendiente() + montoMulta);
                    }
                }
            }
        }

        // 4. Guardamos todos los usuarios actualizados de golpe
        usuarioDao.saveAll(todosLosUsuarios);
        System.out.println("🤖 [SISTEMA] Cálculo de multas finalizado con éxito.");
    }
}
