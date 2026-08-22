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

    // 1. Restauramos tu contraseña para que apunte a la columna correcta y segura
    private String password;

    // 2. Truco: Creamos un "campo fantasma" solo para que MySQL deje de quejarse
    // al crear nuevos usuarios, llenando esa columna vieja obligatoria.
    @Column(name = "contrasena")
    private String contrasenaVieja = "legacy";
    
    private String rol;

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

    // Atributo para el control de morosidad (en colones)
    private Double multaPendiente = 0.0;

    @Column(name = "multa_danos")
    private Double multaDanos = 0.0;

}
