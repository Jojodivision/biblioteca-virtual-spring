package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.ConfiguracionDao;
import com.biblioteca.virtual.domain.Configuracion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConfiguracionServiceImpl implements ConfiguracionService {

    @Autowired
    private ConfiguracionDao configuracionDao;

    @Override
    @Transactional
    public Configuracion obtenerConfiguracion() {
        // Buscamos la fila 1. Si no existe, la creamos al vuelo.
        return configuracionDao.findById(1L).orElseGet(() -> {
            Configuracion configDefault = new Configuracion();
            return configuracionDao.save(configDefault);
        });
    }

    @Override
    @Transactional
    public void guardarConfiguracion(Configuracion configuracion) {
        configuracion.setId(1L); // Candado de seguridad para no crear más filas
        configuracionDao.save(configuracion);
    }
}
