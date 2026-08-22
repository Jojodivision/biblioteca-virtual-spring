package com.biblioteca.virtual.service;

import com.biblioteca.virtual.domain.Configuracion;

public interface ConfiguracionService {
    Configuracion obtenerConfiguracion();
    void guardarConfiguracion(Configuracion configuracion);
}
