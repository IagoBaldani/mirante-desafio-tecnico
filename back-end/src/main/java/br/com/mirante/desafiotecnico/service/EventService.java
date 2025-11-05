package br.com.mirante.desafiotecnico.service;

import br.com.mirante.desafiotecnico.domain.Event;
import br.com.mirante.desafiotecnico.domain.dto.EventRequestRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventResponseRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.PaginatedEventsRepresentation;
import br.com.mirante.desafiotecnico.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@Component
@RequiredArgsConstructor
public class EventService {

    private final EventRepository repository;

    public EventResponseRepresentation getById(Long id){
        return repository.findByIdAndDeletedAtIsNull(id)
                .map(EventResponseRepresentation::fromEvent)
                .orElse(null);
    }

    public PaginatedEventsRepresentation getListPaginated(Integer page, Integer size){
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.ASC, "id"));

        Page<Event> result = repository.findByDeletedAtIsNull(pageable);

        return new PaginatedEventsRepresentation(
                result.getContent().stream().map(EventResponseRepresentation::fromEvent).toList(),
                result.getNumber(),
                result.getSize(),
                result.getTotalPages(),
                result.getTotalElements()
        );
    }

    public EventResponseRepresentation create(EventRequestRepresentation request){
        Event evento = Event.builder()
                .titulo(request.titulo())
                .descricao(request.descricao())
                .dataHora(request.dataHora())
                .local(request.local())
                .ativo(true)
                .build();

        Event saved = repository.save(evento);

        return EventResponseRepresentation.fromEvent(saved);
    }

    public EventResponseRepresentation update(Long id, EventRequestRepresentation request){

        Optional<Event> optional = repository.findByIdAndDeletedAtIsNull(id);

        if(optional.isEmpty()){
            return null;
        }

        Event evento = optional.get();
        evento.setDescricao(request.descricao());
        evento.setLocal(request.local());
        evento.setTitulo(request.titulo());
        evento.setDataHora(request.dataHora());

        return EventResponseRepresentation.fromEvent(repository.save(evento));
    }

    public EventResponseRepresentation delete(Long id){
        Optional<Event> optional = repository.findByIdAndDeletedAtIsNull(id);

        if (optional.isEmpty()) {
            return null;
        }

        Event evento = optional.get();
        evento.softDelete();

        return EventResponseRepresentation.fromEvent(repository.save(evento));
    }


}
