package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Usuario;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

// Le agregamos el nombre "userDetailsService" para que tu SecurityConfig lo encuentre automáticamente
@Service("userDetailsService") 
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    @Autowired
    private UsuarioDao usuarioDao;

    // ====================================================================
    // 1. MÉTODOS DE ADMINISTRACIÓN DE PERFILES (De tu compañero)
    // ====================================================================

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

    // ====================================================================
    // 2. MOTOR DE SEGURIDAD Y LOGIN (Tu código de Spring Security)
    // ====================================================================

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. Buscamos al usuario en la base de datos de Aiven
        Usuario usuario = usuarioDao.findByUsername(username);

        // 2. Si no existe, lanzamos un error
        if (usuario == null) {
            throw new UsernameNotFoundException("El usuario " + username + " no existe");
        }

        // 3. Extraemos su rol (ej. ROLE_ADMIN o ROLE_USER)
        var roles = new ArrayList<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(usuario.getRol()));

        // 4. Retornamos el usuario en el formato de Spring Security
        return new User(usuario.getUsername(), usuario.getPassword(), roles);
    }
}