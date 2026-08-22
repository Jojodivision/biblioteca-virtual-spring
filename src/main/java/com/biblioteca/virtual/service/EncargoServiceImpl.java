package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.EncargoDao;
import com.biblioteca.virtual.domain.Encargo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class EncargoServiceImpl implements EncargoService {

    @Autowired
    private EncargoDao encargoDao;

    @Override
    @Transactional(readOnly = true)
    public List<Encargo> getTodosLosEncargos() {
        return encargoDao.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Encargo> getEncargosPorUsuario(String username) {
        return encargoDao.findByUsuarioUsername(username);
    }

    @Override
    @Transactional
    public void guardarEncargo(Encargo encargo) {
        encargoDao.save(encargo);
    }

    @Override
    @Transactional(readOnly = true)
    public Encargo buscarEncargo(Long idEncargo) {
        return encargoDao.findById(idEncargo).orElse(null);
    }

    @Override
    @Transactional
    public void actualizarEstado(Long idEncargo, String nuevoEstado) {
        Encargo encargo = buscarEncargo(idEncargo);
        if (encargo != null) {
            encargo.setEstado(nuevoEstado);
            encargoDao.save(encargo);
        }
    }
}
