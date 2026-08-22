package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Encargo;
import java.util.List;

public interface EncargoService {
    
    List<Encargo> getTodosLosEncargos();
    
    List<Encargo> getEncargosPorUsuario(String username);
    
    void guardarEncargo(Encargo encargo);
    
    Encargo buscarEncargo(Long idEncargo);
    
    void actualizarEstado(Long idEncargo, String nuevoEstado);
}
