package com.biblioteca.virtual.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private UserDetailsService userDetailsService;

    // Herramienta para desencriptar y comparar las contraseñas
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // Conectamos tu UsuarioService con el encriptador (Versión actualizada)
    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        // ¡El cambio está aquí! Pasamos el servicio directamente en el constructor
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
        
        // El encriptador de contraseñas sí se sigue configurando igual
        authProvider.setPasswordEncoder(passwordEncoder());
        
        return authProvider;
    }

    // Configuramos las reglas de las rutas (URLs)
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests((requests) -> requests
                // Reglas de acceso...
                .anyRequest().authenticated()
        )
        .formLogin((form) -> form
                .loginPage("/login")
                .permitAll()
        )
        .logout((logout) -> logout
                .logoutSuccessUrl("/") // <--- ESTO ES LO QUE TE REGRESA AL INICIO
                .permitAll()
        );
        
        return http.build();
    }
}