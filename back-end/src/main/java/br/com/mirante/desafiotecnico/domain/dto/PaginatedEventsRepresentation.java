package br.com.mirante.desafiotecnico.domain.dto;

import java.util.List;

public record PaginatedEventsRepresentation(
        List<EventResponseRepresentation> content,
        int page,
        int size,
        int totalPages,
        long totalElements
) {}
