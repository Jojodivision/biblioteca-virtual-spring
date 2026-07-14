package com.biblioteca.virtual.domain;

import jakarta.persistence.*;
import java.io.Serializable;
import lombok.Data;

@Data
@Entity
@Table(name = "libro")
public class Libro implements Serializable {
    
    private static final long serialVersionUID = 1L;
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_libro")
    private Long idLibro;
    
    private String titulo;
    private String autor;
    private String editorial;
    
    @Column(name = "anio_publicacion")
    private int anioPublicacion;
    
    private int cantidad;

}