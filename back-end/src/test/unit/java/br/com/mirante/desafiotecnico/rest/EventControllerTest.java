package br.com.mirante.desafiotecnico.rest;

import br.com.mirante.desafiotecnico.domain.dto.ErrorRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventRequestRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventResponseRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.PaginatedEventsRepresentation;
import br.com.mirante.desafiotecnico.service.EventService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class EventControllerTest {

    @InjectMocks
    private EventController tested;

    @Mock
    private EventService service;

    @Nested
    @DisplayName("GET /api/events")
    class GetListPaginatedTests {

        @Test
        @DisplayName("Deve retornar 200")
        void listPaginated_ok(){
            var content = List.of(
                    new EventResponseRepresentation(1L, "Meetup", "Tech", LocalDateTime.of(2025,12,1,19,0), "Av. Brasil"),
                    new EventResponseRepresentation(2L, "Seminário", "Dados", LocalDateTime.of(2026,1,5,10,0), "Teatro")
            );
            var page = new PaginatedEventsRepresentation(content, 0, 2, 5, 10);

            Mockito.when(service.getListPaginated(0, 2)).thenReturn(page);

            ResponseEntity<PaginatedEventsRepresentation> response = tested.getListPaginated(0, 2);

            verify(service).getListPaginated(0, 2);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody().size()).isEqualTo(2);
            assertThat(response.getBody().page()).isEqualTo(0);
            assertThat(response.getBody().content()).isEqualTo(content);
        }

    }

    @Nested
    @DisplayName("GET /api/events/{id}")
    class GetByIdTests {

        @Test
        @DisplayName("Deve retornar 200")
        void getById_found(){
            var id = 1L;
            var resp = new EventResponseRepresentation(
                    id,
                    "Meetup",
                    "Tech",
                    LocalDateTime.of(2025,12,1,19,0),
                    "Av. Brasil"
            );
            Mockito.when(service.getById(id)).thenReturn(resp);

            ResponseEntity<?> response = tested.getById(id);

            verify(service).getById(1L);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        }

        @Test
        @DisplayName("Deve retornar 404")
        void getById_notFound(){
            var id = 999L;
            Mockito.when(service.getById(id)).thenReturn(null);

            ResponseEntity<?> response = tested.getById(999L);

            verify(service).getById(id);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isInstanceOf(ErrorRepresentation.class);
        }
    }

    @Nested
    @DisplayName("POST /api/events")
    class CreateTests {
        @Test
        @DisplayName("Deve criar e retornar 201")
        void create_created(){
            var request = new EventRequestRepresentation(
                    "Workshop",
                    "Foto",
                    LocalDateTime.of(2025,12,10,18,0),
                    "Rua das Flores"
            );
            var created = new EventResponseRepresentation(
                    10L,
                    request.titulo(),
                    request.descricao(),
                    request.dataHora(),
                    request.local()
            );

            Mockito.when(service.create(any(EventRequestRepresentation.class))).thenReturn(created);

            ResponseEntity<?> response = tested.create(request);

            verify(service).create(any(EventRequestRepresentation.class));
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
            assertThat(response.getBody()).isInstanceOf(EventResponseRepresentation.class);
            assertThat(response.getBody()).isEqualTo(created);
        }

    }

    @Nested
    @DisplayName("PUT /api/events/{id}")
    class UpdateTests {
        @Test
        @DisplayName("Deve retornar 200")
        void update_ok(){
            var id = 10L;
            var request = new EventRequestRepresentation(
                    "Workshop",
                    "Foto",
                    LocalDateTime.of(2025,12,10,18,0),
                    "Rua das Flores"
            );
            var updated = new EventResponseRepresentation(
                    id,
                    request.titulo(),
                    request.descricao(),
                    request.dataHora(),
                    request.local()
            );

            Mockito.when(service.update(id, request)).thenReturn(updated);

            ResponseEntity<?> response = tested.update(id, request);

            verify(service).update(id, request);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
            assertThat(response.getBody()).isInstanceOf(EventResponseRepresentation.class);
            assertThat(response.getBody()).isEqualTo(updated);
        }


        @Test
        @DisplayName("Deve retornar 404")
        void update_notFound(){
            var id = 999L;
            var request = new EventRequestRepresentation(
                    "Workshop",
                    "Foto",
                    LocalDateTime.of(2025,12,10,18,0),
                    "Rua das Flores"
            );

            Mockito.when(service.update(id, request)).thenReturn(null);

            ResponseEntity<?> response = tested.update(id, request);

            verify(service).update(id, request);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isInstanceOf(ErrorRepresentation.class);
        }
    }

    @Nested
    @DisplayName("DELETE /api/events/{id}")
    class SoftDeleteTests {
        @Test
        @DisplayName("Deve retornar 204")
        void delete_noContent(){
            var id = 1L;
            var resp = new EventResponseRepresentation(
                    id,
                    "Meetup",
                    "Tech",
                    LocalDateTime.of(2025,12,1,19,0),
                    "Av. Brasil"
            );
            Mockito.when(service.delete(id)).thenReturn(resp);

            ResponseEntity<?> response = tested.softDelete(id);

            verify(service).delete(id);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        }

        @Test
        @DisplayName("Deve retornar 404")
        void delete_notFound() throws Exception {
            var id = 999L;
            Mockito.when(service.delete(id)).thenReturn(null);

            ResponseEntity<?> response = tested.softDelete(id);

            verify(service).delete(id);
            verifyNoMoreInteractions(service);
            assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
            assertThat(response.getBody()).isInstanceOf(ErrorRepresentation.class);
        }
    }
}