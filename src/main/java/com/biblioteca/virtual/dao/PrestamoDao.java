package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Prestamo;
import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoDao extends JpaRepository<Prestamo, Long> {
    
    // Spring Data crea la consulta SQL para traer todos los recibos de una persona
    List<Prestamo> findByUsuario(Usuario usuario);
    
}
