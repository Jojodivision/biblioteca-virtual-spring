package com.biblioteca.virtual.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario;
    
    // --- Credenciales de acceso ---
    @Column(unique = true, nullable = false)
    private String username;
    
    @Column(nullable = false)
    private String password;
    
    // --- Información del perfil ---
    private String nombre;
    private String correo;
    
    // --- Nivel de acceso ---
    // Aquí guardaremos si es "ROLE_USER" (Lector) o "ROLE_ADMIN" (Bibliotecario)
    private String rol;
}
