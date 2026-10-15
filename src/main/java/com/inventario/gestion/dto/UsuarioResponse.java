package com.inventario.gestion.dto;

import com.inventario.gestion.model.Usuario;

/**
 * Lo que devuelve la API de un usuario. Nunca incluye la contrasena.
 */
public record UsuarioResponse(
        Long id,
        String nombre,
        String usuario,
        String rol
) {
    public static UsuarioResponse desde(Usuario u) {
        return new UsuarioResponse(u.getId(), u.getNombre(), u.getUsuario(), u.getRol().getNombre());
    }
}
