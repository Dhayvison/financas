package com.example.financas.exception; // 👈 PACOTE SUGERIDO

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

// Esta anotação garante que, quando esta exceção for lançada, o Spring retorne o status HTTP 404
@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {

    // Construtor que aceita uma mensagem de erro
    public ResourceNotFoundException(String message) {
        super(message);
    }

    // Opcional: Construtor para serialização
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}