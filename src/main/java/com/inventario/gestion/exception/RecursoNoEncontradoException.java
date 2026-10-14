package com.inventario.gestion.exception;

/** Se lanza cuando no existe el registro buscado. Responde 404. */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
