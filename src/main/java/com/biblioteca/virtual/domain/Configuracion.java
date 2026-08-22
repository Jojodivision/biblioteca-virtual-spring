package com.biblioteca.virtual.domain;

import lombok.Data;
import jakarta.persistence.*;

@Data
@Entity
@Table(name = "configuracion")
public class Configuracion {

    @Id
    private Long id = 1L; // Obligamos a que siempre sea la fila 1

    @Column(name = "multa_diaria", nullable = false)
    private Double multaDiaria = 500.0; // Valor por defecto

    @Column(name = "dias_prestamo", nullable = false)
    private Integer diasPrestamo = 7; // Valor por defecto

    @Column(name = "max_libros", nullable = false)
    private Integer maxLibros = 5; // Valor por defecto
}
