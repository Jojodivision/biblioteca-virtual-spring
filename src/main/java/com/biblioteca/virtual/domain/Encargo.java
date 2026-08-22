package com.biblioteca.virtual.domain;

import lombok.Data;
import jakarta.persistence.*; // Si tu NetBeans te da error aquí, cámbialo por javax.persistence.*
import java.time.LocalDate;

@Data
@Entity
@Table(name = "encargo")
public class Encargo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_encargo")
    private Long idEncargo;

    @Column(nullable = false)
    private String titulo;

    private String autor;

    @Column(length = 500)
    private String justificacion;

    // Estados posibles: "Pendiente", "Aprobado", "Rechazado"
    @Column(nullable = false)
    private String estado = "Pendiente";

    @Column(name = "fecha_solicitud")
    private LocalDate fechaSolicitud = LocalDate.now();

    // Relación: Muchos encargos pueden pertenecer a un solo Usuario
    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;
}