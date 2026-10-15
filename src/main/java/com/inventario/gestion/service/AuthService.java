package com.inventario.gestion.service;

import com.inventario.gestion.dto.LoginRequest;
import com.inventario.gestion.dto.LoginResponse;
import com.inventario.gestion.model.Usuario;
import com.inventario.gestion.repository.UsuarioRepository;
import com.inventario.gestion.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;

    /**
     * Verifica usuario y contrasena (BCrypt). Si son incorrectos, Spring lanza
     * BadCredentialsException y el GlobalExceptionHandler responde 401.
     */
    public LoginResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.usuario(), request.password()));

        Usuario usuario = usuarioRepository.findByUsuario(request.usuario()).orElseThrow();
        String rol = usuario.getRol().getNombre();
        String token = jwtService.generarToken(usuario.getUsuario(), rol);

        return new LoginResponse(token, "Bearer", usuario.getUsuario(), usuario.getNombre(), rol);
    }
}
