package com.inventario.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

/**
 * Datos que llegan en el POST y PUT de /api/productos.
 * El stock solo se usa al registrar; en el PUT se ignora.
 */
public record ProductoRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede tener mas de 200 caracteres")
        String descripcion,

        @NotNull(message = "El precio es obligatorio")
        @Positive(message = "El precio debe ser mayor que 0")
        BigDecimal precio,

        @PositiveOrZero(message = "El stock debe ser mayor o igual que 0")
        Integer stock,

        @NotNull(message = "El stock minimo es obligatorio")
        @PositiveOrZero(message = "El stock minimo debe ser mayor o igual que 0")
        Integer stockMinimo,

        @NotNull(message = "La categoria es obligatoria")
        Long idCategoria
) {
}
