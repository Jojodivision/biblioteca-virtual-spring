package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Libro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LibroDao extends JpaRepository<Libro, Long> {
// Busca coincidencias parciales en título o autor ignorando mayúsculas/minúsculas

    java.util.List<Libro> findByTituloContainingIgnoreCaseOrAutorContainingIgnoreCase(String titulo, String autor);

}
