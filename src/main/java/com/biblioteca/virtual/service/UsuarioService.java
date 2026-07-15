package com.biblioteca.virtual.service;

import com.biblioteca.virtual.dao.UsuarioDao;
import com.biblioteca.virtual.domain.Usuario;
import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// Le indicamos a Spring que este es el servicio oficial para cargar usuarios
@Service("userDetailsService")
public class UsuarioService implements UserDetailsService {

    @Autowired
    private UsuarioDao usuarioDao;

    @Override
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
