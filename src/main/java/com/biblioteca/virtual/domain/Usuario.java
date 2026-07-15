package com.biblioteca.virtual.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "usuario")
public class Usuario implements Serializable {
    
    private static final long serialVersionUID = 1L;

    // --- TU MOTOR DE SEGURIDAD ---
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Long idUsuario; 

    @Column(unique = true, nullable = false)
    private String username; 

    @Column(nullable = false)
    private String password; 

    private String rol; 

    // --- EL PERFIL DE TU COMPAÑERO ---
    @Column(unique = true)
    private Long identificacion; 

    @Column(nullable = false)
    private String nombre;

    @Column(name = "primer_apellido")
    private String primerApellido;

    @Column(name = "segundo_apellido")
    private String segundoApellido;

    @Column(name = "correo_electronico", unique = true, nullable = false)
    private String correo; 

    private String telefono;

    private boolean activo = true;




}
