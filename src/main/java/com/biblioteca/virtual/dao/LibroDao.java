package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroDao extends JpaRepository<Libro, Long> {
    
}