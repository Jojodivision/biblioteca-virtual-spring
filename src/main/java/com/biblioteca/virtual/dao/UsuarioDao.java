package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDao extends JpaRepository<Usuario, Long> {
    
    // ¡Magia de Spring Data JPA! 
    // Solo con escribir "findByUsername", Spring crea la consulta SQL exacta por detrás
    // para buscar al usuario en la base de datos sin que escribas el Query manualmente.
    Usuario findByUsername(String username);
    
}
