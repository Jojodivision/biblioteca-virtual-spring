package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Usuario;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> getUsuarios() {
        return usuarioDao.findAll();
    }

    @Override
    @Transactional
    public void save(Usuario usuario) {
        usuarioDao.save(usuario);
    }

    @Override
    @Transactional
    public void delete(Usuario usuario) {
        usuarioDao.delete(usuario);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario getUsuario(Usuario usuario) {
        return usuarioDao.findById(usuario.getIdentificacion()).orElse(null);
    }

    @Override
    public boolean existeCorreo(String correo) {
        return usuarioDao.existsByCorreoElectronico(correo);
    }

    @Override
    public boolean existeIdentificacion(Long identificacion) {
        return usuarioDao.existsByIdentificacion(identificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorIdentificacion(Long identificacion) {
        return usuarioDao.findByIdentificacion(identificacion);
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorCorreo(String correo) {
        return usuarioDao.findByCorreoElectronico(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioDao.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existeCorreoParaOtroUsuario(String correo, Long identificacion) {
        return usuarioDao.existsByCorreoElectronicoAndIdentificacionNot(correo, identificacion);
    }

}