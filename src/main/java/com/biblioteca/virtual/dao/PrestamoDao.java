package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Prestamo;
import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PrestamoDao extends JpaRepository<Prestamo, Long> {
    
    // Spring Data crea la consulta SQL para traer todos los recibos de una persona
    List<Prestamo> findByUsuario(Usuario usuario);
    
        // validar si existen registros en ese rango de fechas (evitar reportes vacios)
    boolean existsByFechaPrestamoBetween(java.time.LocalDate inicio, java.time.LocalDate fin);

    // obtener los libros mas prestados
    @org.springframework.data.jpa.repository.Query(value = "SELECT l.titulo, COUNT(p.id_prestamo) as total FROM prestamo p INNER JOIN libro l ON p.id_libro = l.id_libro WHERE p.fecha_prestamo BETWEEN :inicio AND :fin GROUP BY l.id_libro ORDER BY total DESC LIMIT 10", nativeQuery = true)
    java.util.List<Object[]> findLibrosMasPrestados(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio, @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin);

    // obtener las horas de mayor flujo
    @org.springframework.data.jpa.repository.Query(value = "SELECT HOUR(p.fecha_prestamo) as hora, COUNT(p.id_prestamo) as total FROM prestamo p WHERE p.fecha_prestamo BETWEEN :inicio AND :fin GROUP BY HOUR(p.fecha_prestamo) ORDER BY total DESC", nativeQuery = true)
    java.util.List<Object[]> findHorasMayorFlujo(@org.springframework.data.repository.query.Param("inicio") java.time.LocalDate inicio, @org.springframework.data.repository.query.Param("fin") java.time.LocalDate fin);
    
}
