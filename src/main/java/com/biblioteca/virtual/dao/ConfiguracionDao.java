package com.biblioteca.virtual.dao;

import com.biblioteca.virtual.domain.Configuracion;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConfiguracionDao extends JpaRepository<Configuracion, Long> {
}
