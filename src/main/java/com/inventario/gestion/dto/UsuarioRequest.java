package com.inventario.gestion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que llegan en el POST y PUT de /api/usuarios.
 */
public record UsuarioRequest(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(max = 100, message = "El nombre no puede tener mas de 100 caracteres")
        String nombre,

        @NotBlank(message = "El usuario es obligatorio")
        @Size(min = 4, max = 50, message = "El usuario debe tener entre 4 y 50 caracteres")
        String usuario,

        @NotBlank(message = "La contrasena es obligatoria")
        @Size(min = 6, max = 50, message = "La contrasena debe tener entre 6 y 50 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio (ADMIN o EMPLEADO)")
        String rol
) {
}
