package com.tecsup.categoria_service.exception;

/**
 * Se lanza cuando un recurso solicitado no existe (se traduce a HTTP 404).
 */
public class RecursoNoEncontradoException extends RuntimeException {
    public RecursoNoEncontradoException(String mensaje) {
        super(mensaje);
    }
}
