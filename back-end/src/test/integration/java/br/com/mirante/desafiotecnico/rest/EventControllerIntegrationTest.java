package br.com.mirante.desafiotecnico.rest;

import br.com.mirante.desafiotecnico.domain.dto.EventRequestRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.PaginatedEventsRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventResponseRepresentation;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.http.*;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
class EventControllerIntegrationTest {

    @LocalServerPort
    int port;

    @Autowired
    TestRestTemplate rest;

    private String url(String path) {
        return "http://localhost:" + port + path;
    }

    @Test
    void deveCriarListarEBuscarEvento() {
        // 1) Cria evento
        EventRequestRepresentation body = new EventRequestRepresentation(
                "Workshop de Fotografia",
                "Técnicas e prática",
                LocalDateTime.of(2025, 12, 10, 18, 0),
                "Rua das Flores, 200 - Londrina/PR"
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<EventRequestRepresentation> entity = new HttpEntity<>(body, headers);

        ResponseEntity<EventResponseRepresentation> postResp = rest.postForEntity(url("/api/events"), entity, EventResponseRepresentation.class);

        assertThat(postResp.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        EventResponseRepresentation created = postResp.getBody();
        assertThat(created).isNotNull();
        assertThat(created.id()).isNotNull();
        Long id = created.id();

        // 2) Faz a consulta paginada
        ResponseEntity<PaginatedEventsRepresentation> listResp =
                rest.getForEntity(url("/api/events?page=0&size=10"), PaginatedEventsRepresentation.class);

        assertThat(listResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(listResp.getBody()).isNotNull();
        assertThat(listResp.getBody().content()).isNotEmpty();

        // 3) Faz a consulta por ID
        ResponseEntity<EventResponseRepresentation> getResp =
                rest.getForEntity(url("/api/events/" + id), EventResponseRepresentation.class);

        assertThat(getResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(getResp.getBody()).isNotNull();
        assertThat(getResp.getBody().id()).isEqualTo(id);
        assertThat(getResp.getBody().titulo()).isEqualTo("Workshop de Fotografia");
    }

    @Test
    void deveAtualizarEDeletarEvento() {
        // 1) Cria evento
        EventRequestRepresentation body = new EventRequestRepresentation(
                "Meetup",
                "Tech",
                LocalDateTime.of(2025, 12, 1, 19, 0),
                "Av. Brasil, 1000 - Londrina/PR"
        );
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        ResponseEntity<EventResponseRepresentation> postResp =
                rest.postForEntity(url("/api/events"), new HttpEntity<>(body, headers), EventResponseRepresentation.class);

        Long id = postResp.getBody().id();

        // 2) Atualiza o evento criado.
        EventRequestRepresentation update = new EventRequestRepresentation(
                "Meetup Atualizado",
                "Tech Atualizado",
                LocalDateTime.of(2025, 12, 2, 20, 0),
                "Teatro Municipal"
        );
        ResponseEntity<EventResponseRepresentation> putResp =
                rest.exchange(url("/api/events/" + id), HttpMethod.PUT,
                        new HttpEntity<>(update, headers), EventResponseRepresentation.class);

        assertThat(putResp.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(putResp.getBody()).isNotNull();
        assertThat(putResp.getBody().titulo()).isEqualTo("Meetup Atualizado");

        // 3) Realiza a exclusao logica do evento criado.
        ResponseEntity<Void> delResp =
                rest.exchange(url("/api/events/" + id), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);

        assertThat(delResp.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        // 4) Consulta apos exclusao logica
        ResponseEntity<String> getAfterDel =
                rest.getForEntity(url("/api/events/" + id), String.class);
        assertThat(getAfterDel.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
