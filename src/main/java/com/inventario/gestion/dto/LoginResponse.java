package com.inventario.gestion.dto;

public record LoginResponse(
        String token,
        String tipo,
        String usuario,
        String nombre,
        String rol
) {
}
