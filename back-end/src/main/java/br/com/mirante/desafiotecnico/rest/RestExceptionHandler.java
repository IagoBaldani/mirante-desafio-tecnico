package br.com.mirante.desafiotecnico.rest;


import br.com.mirante.desafiotecnico.domain.dto.ErrorRepresentation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.util.stream.Collectors;

@RestControllerAdvice
@Slf4j
public class RestExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorRepresentation> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult().getFieldErrors().stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining("; "));
        log.error(".: {} -> 400 - BAD_REQUEST", message);

        return ResponseEntity.badRequest().body(ErrorRepresentation.create(400, "Bad Request", message, currentPath()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorRepresentation> handleGeneric(Exception ex) {
        log.error(".: {} -> 500 - INTERNAL_SERVER_ERROR ", ex.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ErrorRepresentation.create(500, "Internal Server Error", ex.getMessage(), currentPath()));
    }

    public static ResponseEntity<ErrorRepresentation> notFound(String path) {
        ErrorRepresentation representation = ErrorRepresentation.create(404, "Not Found", "Evento não encontrado", path);
        log.error(".: {} -> 404 - NOT_FOUND ", representation.message());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(representation);
    }

    private static String currentPath() {
        try {
            return ServletUriComponentsBuilder.fromCurrentRequest().build().getPath();
        } catch (Exception e) {
            return "/api/events";
        }
    }
}
