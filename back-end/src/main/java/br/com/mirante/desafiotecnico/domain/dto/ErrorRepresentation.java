package br.com.mirante.desafiotecnico.domain.dto;

public record ErrorRepresentation(String timestamp, int status, String error, String message, String path) {

    public static ErrorRepresentation create(int status, String error, String message, String path) {
        return new ErrorRepresentation(
                java.time.LocalDateTime.now().toString(),
                status,
                error,
                message,
                path
        );
    }
}