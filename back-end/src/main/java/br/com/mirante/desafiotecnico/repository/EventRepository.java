package br.com.mirante.desafiotecnico.repository;

import br.com.mirante.desafiotecnico.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

    @Query("SELECT e FROM Event e WHERE e.ativo = true")
    Page<Event> findByDeletedAtIsNull(Pageable pageable);

    @Query("SELECT e FROM Event e WHERE e.id = :id AND e.ativo = true")
    Optional<Event> findByIdAndDeletedAtIsNull(Long id);
}
