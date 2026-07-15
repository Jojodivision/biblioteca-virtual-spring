package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Prestamo;
import java.util.List;

public interface PrestamoService {
    
    // Intenta hacer la transacción, si falla (ej. no hay stock), lanza un error
    void realizarPrestamo(Long idLibro, String username) throws Exception;
    
    // Obtiene el historial del estudiante
    List<Prestamo> obtenerPrestamosPorUsername(String username);
    
    // Intenta devolver el libro y sumar el stock
    void devolverLibro(Long idPrestamo, String username) throws Exception;
}
