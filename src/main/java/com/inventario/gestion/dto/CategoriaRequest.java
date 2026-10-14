package com.inventario.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que llegan en el POST y PUT de /api/categorias.
 */
public record CategoriaRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 50, message = "El nombre no puede tener mas de 50 caracteres")
        String nombre,

        @Size(max = 200, message = "La descripcion no puede tener mas de 200 caracteres")
        String descripcion
) {
}
