package br.com.mirante.desafiotecnico.domain.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

public record EventRequestRepresentation(
        @NotBlank @Size(max = 100) String titulo,
        @Size(max = 1000) String descricao,
        @NotNull LocalDateTime dataHora,
        @NotBlank @Size(max = 200) String local
) {}