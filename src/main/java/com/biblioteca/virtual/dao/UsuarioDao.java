package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioDao extends JpaRepository<Usuario, Long> {

    boolean existsByCorreoElectronico(String correoElectronico);

    boolean existsByIdentificacion(Long identificacion);

    Usuario findByCorreoElectronico(String correoElectronico);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    Usuario findByIdentificacion(Long identificacion);

    boolean existsByCorreoElectronicoAndIdentificacionNot(String correoElectronico, Long identificacion);

}