package br.com.mirante.desafiotecnico.rest;

import br.com.mirante.desafiotecnico.domain.dto.EventRequestRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventResponseRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.PaginatedEventsRepresentation;
import br.com.mirante.desafiotecnico.service.EventService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

import static br.com.mirante.desafiotecnico.rest.RestExceptionHandler.notFound;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@Slf4j
public class EventController {

    private final EventService service;

    // GET /api/events?page=&size=
    @GetMapping
    public ResponseEntity<PaginatedEventsRepresentation> getListPaginated(@RequestParam(name = "page", defaultValue = "0") @Min(0) Integer page, @RequestParam(name = "size", defaultValue = "10") @Min(1) @Max(100) Integer size) {
        log.info(".: GET - /api/events?page={}&size={}", page, size);
        PaginatedEventsRepresentation response = service.getListPaginated(page, size);

        return ResponseEntity.ok(response);
    }

    // GET /api/events/{id}
    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable("id") Long id) {
        log.info(".: GET - /api/events/{}", id);
        EventResponseRepresentation response = service.getById(id);

        if(response == null){
            return notFound("GET - /api/events/" + id);
        }

        return ResponseEntity.ok(response);
    }

    // POST /api/events
    @PostMapping
    public ResponseEntity<EventResponseRepresentation> create(@Valid @RequestBody EventRequestRepresentation request) {
        log.info(".: POST - /api/events/");
        EventResponseRepresentation response = service.create(request);

        URI location = ServletUriComponentsBuilder
                .fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(response.id())
                .toUri();

        return ResponseEntity.created(location).body(response);
    }

    // PUT /api/events/{id}
    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable("id") Long id, @Valid @RequestBody EventRequestRepresentation request) {
        log.info(".: PUT - /api/events/{}", id);
        EventResponseRepresentation response = service.update(id, request);

        if(response == null){
            return notFound("PUT - /api/events/" + id);
        }

        return ResponseEntity.ok(response);
    }

    // DELETE (soft) /api/events/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<?> softDelete(@PathVariable("id") Long id) {
        log.info(".: DELETE - /api/events/{}", id);
        EventResponseRepresentation response = service.delete(id);

        if(response == null){
            return notFound("DELETE - /api/events/" + id);
        }

        return ResponseEntity.noContent().build();
    }

}
