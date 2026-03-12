package com.juliandonati.api.repository;

import com.juliandonati.api.domain.Event;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository // Es una buena práctica usar esta anotación.
public interface EventRepository extends JpaRepository<Event, Long > {
    Page<Event> findByNameContainingIgnoreCase(String name, Pageable pageable);

    // PROBLEMÁTICA DE N + 1...

    // SOLUCIÓN 1: Join Fetch (Recomendada)
    @Query("SELECT e FROM Event e JOIN FETCH e.category LEFT JOIN FETCH e.speakers")
    // Es muy parecido a SQL el JPQL (Java Persistence Query Language)
    // Haciendo este metodo específico podemos solucionar el problema N + 1.
    List<Event> findAllWithCategoryAndSpeakers();

    @Query("SELECT e FROM Event e JOIN FETCH e.category LEFT JOIN FETCH e.speakers WHERE e.id = :id")
    // Para poner el parámetro en la Query es con ':' antes.
    Optional<Event> findByIdWithCategoryAndSpeakers(Long id);


    // SOLUCION 2: EntityGraph
    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category","speakers"})
    List<Event> findAll();


    @Override
    @NonNull
    @EntityGraph(attributePaths = {"category","speakers"})
    Optional<Event> findById(Long id);

    @NonNull
    @EntityGraph(attributePaths = {"category","speakers","attendedUsers"})
    @Query("SELECT e FROM Event e")
    List<Event> findAllWithDetails();
}
