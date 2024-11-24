package com.example.exception;

public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException() {
        super();
    }

    public ResourceNotFoundException(String mensaje) {
        super(mensaje);
    }

    public ResourceNotFoundException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
