package com.biblioteca.virtual.config;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Usuario;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private UsuarioDao usuarioDao;

    @Value("${admin.password}")
    private String adminPassword;

    @Value("${user1.password}")
    private String user1Password;

    @Value("${user2.password}")
    private String user2Password;

    @Override
    public void run(String... args) throws Exception {
        if (usuarioDao.count() == 0) {
            log.info("Tabla de usuarios vacía. Inicializando usuarios con contraseñas encriptadas...");
            
            // Instanciamos el encriptador de Spring Security
            BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

            Usuario admin = new Usuario();
            admin.setUsername("admin_biblioteca");
            admin.setPassword(encoder.encode(adminPassword)); // Encriptamos antes de guardar
            admin.setNombre("Administrador Principal");
            admin.setCorreo("admin@biblioteca.com");
            admin.setRol("ROLE_ADMIN");
            usuarioDao.save(admin);

            Usuario estudiante1 = new Usuario();
            estudiante1.setUsername("estudiante_01");
            estudiante1.setPassword(encoder.encode(user1Password)); // Encriptamos antes de guardar
            estudiante1.setNombre("Juan Pérez");
            estudiante1.setCorreo("juan@estudiante.com");
            estudiante1.setRol("ROLE_USER");
            usuarioDao.save(estudiante1);

            Usuario estudiante2 = new Usuario();
            estudiante2.setUsername("estudiante_02");
            estudiante2.setPassword(encoder.encode(user2Password)); // Encriptamos antes de guardar
            estudiante2.setNombre("María Gómez");
            estudiante2.setCorreo("maria@estudiante.com");
            estudiante2.setRol("ROLE_USER");
            usuarioDao.save(estudiante2);

            log.info("¡Usuarios creados y asegurados exitosamente!");
        } else {
            log.info("Los usuarios ya existen. Saltando inicialización.");
        }
    }
}
