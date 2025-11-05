package br.com.mirante.desafiotecnico.domain.dto;

import java.time.LocalDateTime;
import br.com.mirante.desafiotecnico.domain.Event;

public record EventResponseRepresentation(Long id, String titulo, String descricao, LocalDateTime dataHora, String local) {

    public static EventResponseRepresentation fromEvent(Event event) {
        return new EventResponseRepresentation(
                event.getId(),
                event.getTitulo(),
                event.getDescricao(),
                event.getDataHora(),
                event.getLocal()
        );
    }
}