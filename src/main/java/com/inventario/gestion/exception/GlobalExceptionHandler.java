package com.inventario.gestion.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manejo de errores comun para TODOS los modulos.
 * Cualquiera puede lanzar RecursoNoEncontradoException (404) o ReglaNegocioException (409)
 * y la respuesta sale con el mismo formato JSON.
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<Map<String, Object>> noEncontrado(RecursoNoEncontradoException e) {
        return respuesta(HttpStatus.NOT_FOUND, e.getMessage(), null);
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<Map<String, Object>> reglaNegocio(ReglaNegocioException e) {
        return respuesta(HttpStatus.CONFLICT, e.getMessage(), null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<Map<String, Object>> loginFallido(AuthenticationException e) {
        return respuesta(HttpStatus.UNAUTHORIZED, "Usuario o contrasena incorrectos", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> validacion(MethodArgumentNotValidException e) {
        Map<String, String> campos = new LinkedHashMap<>();
        e.getBindingResult().getFieldErrors()
                .forEach(err -> campos.put(err.getField(), err.getDefaultMessage()));
        return respuesta(HttpStatus.BAD_REQUEST, "Datos invalidos", campos);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, Object>> jsonInvalido(HttpMessageNotReadableException e) {
        return respuesta(HttpStatus.BAD_REQUEST, "El JSON enviado no es valido", null);
    }

    private ResponseEntity<Map<String, Object>> respuesta(HttpStatus status, String mensaje,
                                                          Map<String, String> errores) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("fecha", LocalDateTime.now().toString());
        body.put("estado", status.value());
        body.put("mensaje", mensaje);
        if (errores != null) {
            body.put("errores", errores);
        }
        return ResponseEntity.status(status).body(body);
    }
}
