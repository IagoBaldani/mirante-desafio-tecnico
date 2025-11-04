package br.com.mirante.desafiotecnico.service;

import br.com.mirante.desafiotecnico.domain.Event;
import br.com.mirante.desafiotecnico.domain.dto.EventRequestRepresentation;
import br.com.mirante.desafiotecnico.domain.dto.EventResponseRepresentation;
import br.com.mirante.desafiotecnico.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @Mock
    private EventRepository repository;

    @InjectMocks
    private EventService tested;

    private Event eventoExistente;

    private EventRequestRepresentation request;

    @BeforeEach
    void setUp() {
        eventoExistente = Event.builder()
                .id(1L)
                .titulo("Meetup")
                .descricao("Tech")
                .dataHora(LocalDateTime.of(2025, 12, 10, 18, 0))
                .local("Av. Brasil, 1000 - Londrina/PR")
                .ativo(true)
                .build();

        request = new EventRequestRepresentation(
                "Workshop",
                "Fotografia",
                LocalDateTime.of(2025, 12, 12, 19, 0),
                "Rua das Flores, 200 - Londrina/PR"
        );
    }

    @Nested
    @DisplayName("CREATE")
    class CreateTests{

        @Test
        @DisplayName("Deve criar com sucesso")
        void create_deveCriarComSucesso() {

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);

            Event saved = Event.builder()
                    .id(10L)
                    .titulo(request.titulo())
                    .descricao(request.descricao())
                    .dataHora(request.dataHora())
                    .local(request.local())
                    .ativo(true)
                    .build();

            when(repository.save(any(Event.class))).thenReturn(saved);

            EventResponseRepresentation response = tested.create(request);

            verify(repository).save(eventCaptor.capture());
            Event toSave = eventCaptor.getValue();

            assertThat(toSave.getId()).isNull();
            assertThat(toSave.getTitulo()).isEqualTo(request.titulo());
            assertThat(toSave.getDescricao()).isEqualTo(request.descricao());
            assertThat(toSave.getDataHora()).isEqualTo(request.dataHora());
            assertThat(toSave.getLocal()).isEqualTo(request.local());
            assertThat(toSave.isAtivo()).isTrue();

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(10L);
            assertThat(response.titulo()).isEqualTo(request.titulo());
            verifyNoMoreInteractions(repository);
        }

    }

    @Nested
    @DisplayName("GETBYID")
    class GetByIdTest{

        @Test
        @DisplayName("Deve consultar com sucesso")
        void getById_deveRetornarEventoEncontrado() {
            when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(eventoExistente));

            EventResponseRepresentation response = tested.getById(1L);

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.titulo()).isEqualTo("Meetup");
            verify(repository).findByIdAndDeletedAtIsNull(1L);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar null")
        void getById_deveRetornarNullQuandoNaoEncontrado() {
            when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

            EventResponseRepresentation response = tested.getById(99L);

            assertThat(response).isNull();
            verify(repository).findByIdAndDeletedAtIsNull(99L);
            verifyNoMoreInteractions(repository);
        }

    }

    @Nested
    @DisplayName("UPDATE")
    class UpdateTest{

        @Test
        @DisplayName("Deve atualizar campos com sucesso")
        void update_deveAtualizarCamposSalvarERetornarResponse() {
            when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(eventoExistente));

            Event updated = Event.builder()
                    .id(eventoExistente.getId())
                    .titulo(request.titulo())
                    .descricao(request.descricao())
                    .dataHora(request.dataHora())
                    .local(request.local())
                    .ativo(true)
                    .build();

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            when(repository.save(any(Event.class))).thenReturn(updated);

            EventResponseRepresentation response = tested.update(1L, request);

            verify(repository).findByIdAndDeletedAtIsNull(1L);
            verify(repository).save(eventCaptor.capture());

            Event persisted = eventCaptor.getValue();
            assertThat(persisted.getId()).isEqualTo(1L);
            assertThat(persisted.getTitulo()).isEqualTo(request.titulo());
            assertThat(persisted.getDescricao()).isEqualTo(request.descricao());
            assertThat(persisted.getDataHora()).isEqualTo(request.dataHora());
            assertThat(persisted.getLocal()).isEqualTo(request.local());
            assertThat(persisted.isAtivo()).isTrue();

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            assertThat(response.titulo()).isEqualTo(request.titulo());

            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar null")
        void update_deveRetornarNullQuandoNaoEncontrado() {
            when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

            EventResponseRepresentation response = tested.update(99L, request);

            assertThat(response).isNull();
            verify(repository).findByIdAndDeletedAtIsNull(99L);
            verifyNoMoreInteractions(repository);
        }
    }

    @Nested
    @DisplayName("DELETE")
    class DeleteTest{

        @Test
        @DisplayName("Deve deletar com sucesso")
        void delete_deveFazerSoftDeleteSalvarERetornarResponse() {
            when(repository.findByIdAndDeletedAtIsNull(1L)).thenReturn(Optional.of(eventoExistente));

            Event afterDelete = Event.builder()
                    .id(eventoExistente.getId())
                    .titulo(eventoExistente.getTitulo())
                    .descricao(eventoExistente.getDescricao())
                    .dataHora(eventoExistente.getDataHora())
                    .local(eventoExistente.getLocal())
                    .ativo(false)
                    .build();

            ArgumentCaptor<Event> eventCaptor = ArgumentCaptor.forClass(Event.class);
            when(repository.save(any(Event.class))).thenReturn(afterDelete);

            EventResponseRepresentation response = tested.delete(1L);

            verify(repository).findByIdAndDeletedAtIsNull(1L);
            verify(repository).save(eventCaptor.capture());

            Event saved = eventCaptor.getValue();
            assertThat(saved.isAtivo()).isFalse();

            assertThat(response).isNotNull();
            assertThat(response.id()).isEqualTo(1L);
            verifyNoMoreInteractions(repository);
        }

        @Test
        @DisplayName("Deve retornar null")
        void delete_deveRetornarNullQuandoNaoEncontrado() {
            when(repository.findByIdAndDeletedAtIsNull(99L)).thenReturn(Optional.empty());

            EventResponseRepresentation response = tested.delete(99L);

            assertThat(response).isNull();
            verify(repository).findByIdAndDeletedAtIsNull(99L);
            verifyNoMoreInteractions(repository);
        }
    }
}
