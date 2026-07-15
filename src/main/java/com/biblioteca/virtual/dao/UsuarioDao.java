package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    // --- MÉTODOS PARA SPRING SECURITY (Tu código) ---
    Usuario findByUsername(String username);

    // --- MÉTODOS PARA ADMINISTRACIÓN DE PERFILES ---
    // Les quitamos la palabra "Electronico" para que coincidan con tu variable "correo"
    boolean existsByCorreo(String correo);

    boolean existsByIdentificacion(Long identificacion);

    Usuario findByCorreo(String correo);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    Usuario findByIdentificacion(Long identificacion);

    boolean existsByCorreoAndIdentificacionNot(String correo, Long identificacion);

}