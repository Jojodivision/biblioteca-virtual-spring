package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    // --- MÉTODOS PARA SPRING SECURITY (Tu código) ---
    // ¡Magia de Spring Data JPA! 
    // Solo con escribir "findByUsername", Spring crea la consulta SQL exacta por detrás
    // para buscar al usuario en la base de datos sin que escribas el Query manualmente.
    Usuario findByUsername(String username);

    // --- MÉTODOS PARA ADMINISTRACIÓN DE PERFILES (Código de tu compañero) ---
    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByIdentificacion(Long identificacion);

    Usuario findByCorreoElectronico(String correoElectronico);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    Usuario findByIdentificacion(Long identificacion);

    boolean existsByCorreoElectronicoAndIdentificacionNot(String correoElectronico, Long identificacion);

}