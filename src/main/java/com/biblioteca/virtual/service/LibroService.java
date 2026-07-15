package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Libro;
import java.util.List;

public interface LibroService {
    
    // Método para obtener todos los libros registrados
    public List<Libro> getLibros();
    
    // Método para guardar o actualizar un libro
    public void save(Libro libro);
    
    // Método para eliminar un libro
    public void delete(Libro libro);
    
    // Método para buscar un libro específico por su ID
    public Libro getLibro(Libro libro);
    // Método para la barra de búsqueda
    java.util.List<Libro> buscarLibros(String termino);
}
