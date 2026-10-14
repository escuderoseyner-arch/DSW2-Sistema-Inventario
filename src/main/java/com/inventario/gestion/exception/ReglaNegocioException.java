package com.inventario.gestion.exception;

/** Se lanza cuando se rompe una regla del negocio (duplicados, borrados no permitidos, etc.). Responde 409. */
public class ReglaNegocioException extends RuntimeException {
    public ReglaNegocioException(String mensaje) {
        super(mensaje);
    }
}
