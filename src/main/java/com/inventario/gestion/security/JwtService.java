package com.inventario.gestion.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

/**
 * Crea y valida los tokens JWT que se entregan al iniciar sesion.
 */
@Service
public class JwtService {

    private final SecretKey clave;
    private final long expiracionMs;

    public JwtService(@Value("${jwt.secret}") String secreto,
                      @Value("${jwt.expiracion-ms}") long expiracionMs) {
        this.clave = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secreto));
        this.expiracionMs = expiracionMs;
    }

    public String generarToken(String usuario, String rol) {
        Date ahora = new Date();
        return Jwts.builder()
                .subject(usuario)
                .claim("rol", rol)
                .issuedAt(ahora)
                .expiration(new Date(ahora.getTime() + expiracionMs))
                .signWith(clave)
                .compact();
    }

    public String obtenerUsuario(String token) {
        return leer(token).getSubject();
    }

    public boolean esValido(String token) {
        try {
            leer(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    private Claims leer(String token) {
        return Jwts.parser()
                .verifyWith(clave)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
