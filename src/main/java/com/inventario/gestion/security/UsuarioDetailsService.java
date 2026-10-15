package com.inventario.gestion.security;

import com.inventario.gestion.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Le dice a Spring Security como buscar un usuario en nuestra base de datos.
 */
@Service
@RequiredArgsConstructor
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByUsuario(username)
                .map(u -> User.withUsername(u.getUsuario())
                        .password(u.getPassword())
                        .roles(u.getRol().getNombre())
                        .build())
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));
    }
}
