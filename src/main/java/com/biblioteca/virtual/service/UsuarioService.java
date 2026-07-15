package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Usuario;
import java.util.List;

public interface UsuarioService {
    
    List<Usuario> getUsuarios();
    
    void save(Usuario usuario);
    
    void delete(Usuario usuario);
    
    Usuario getUsuario(Usuario usuario);
    
    boolean existeCorreo(String correo);
    
    boolean existeIdentificacion(Long identificacion);
    
    Usuario buscarPorIdentificacion(Long identificacion);
    
    Usuario buscarPorCorreo(String correo);
    
    List<Usuario> buscarPorNombre(String nombre);
    
    boolean existeCorreoParaOtroUsuario(String correo, Long identificacion);
}