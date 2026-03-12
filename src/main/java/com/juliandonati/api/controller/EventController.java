package com.juliandonati.api.controller;

import com.juliandonati.api.domain.Event;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.mapper.EventMapper;
import com.juliandonati.api.service.EventService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/events")

@RequiredArgsConstructor

@Tag(name = "Eventos", description = "Operaciones relacionadas con la gestión de eventos")
public class EventController {
    private final EventService eventService;
    private final EventMapper eventMapper;

    private static final Logger logger = LoggerFactory.getLogger(EventController.class);

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<Page<EventResponseDto>> getAllEvents(
            @RequestParam(required = false) String name,
            @PageableDefault(page=0, size=10, sort="name") Pageable pageable){

        logger.debug("Recibida solicitud GET /events con nombre '{}' y paginación {}.",name,pageable);
        Page<EventResponseDto> eventResponseDtoPage = eventService.findAll(name, pageable);
        logger.info("Devolviendo {} eventos paginados",eventResponseDtoPage.getContent().size());
        return ResponseEntity.ok(eventResponseDtoPage);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EventResponseDto> createEvent(@Valid @RequestBody EventRequestDto eventRequestDto
    /* No ponemos el BindingResult porque estamos en una API Rest; ademas que se solía usar en aplicaciones viejas.
    * Le estamos diciendo a Spring que si la validación falla, se encarge éste, lance un MethodArgumentNotValidException
    * que es atrapada por un Handler de Spring que genera una respuesta JSON detallada con un error de "status":400.*/){
        logger.debug("Recibida solicitud para crear evento: {}", eventRequestDto.getName());

        EventResponseDto responseDto = eventMapper.toEventResponseDto(eventService.save(eventRequestDto));

        logger.info("Evento creado exitosamente: {}", responseDto.getName());

        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    @Operation(summary = "Obtener un evento por su id", description = "Devuelve los detalles de un eventos específico por su id.")
    @ApiResponses(value={
            @ApiResponse(responseCode = "200", description = "Evento encontrado exitosamente"),
            @ApiResponse(responseCode = "404", description = "Evento no encontrado")
    })
    public ResponseEntity<EventResponseDto> getEventById(@PathVariable Long id){
        Event event = eventService.findById(id);
        EventResponseDto eventResponseDto = eventMapper.toEventResponseDto(event);
        return ResponseEntity.ok(eventResponseDto);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<EventResponseDto> updateEvent(@PathVariable Long id,
                                                        @RequestBody @Valid EventRequestDto requestDto){

        Event updatedEvent = eventService.update(requestDto, id);

        return ResponseEntity.ok(eventMapper.toEventResponseDto(updatedEvent));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<Void> deleteEvent(@PathVariable Long id){
        eventService.deleteById(id);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/problematic")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Event>> getAllEventsProblematic(){
        List<Event> events = eventService.getAllEventsAndTheirDetailsProblematic();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/optimized")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Event>> getAllEventsOptimizedWithJoinFetch(){
        List<Event> events = eventService.getAllEventsAndTheirDetailsOptimizedWithJoinFetch();

        return ResponseEntity.ok(events);
    }

    @GetMapping("/optimized-with-entitygraph")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<Event>> getAllEventsOptimizedWithEntityGraph(){
        List<Event> events = eventService.getAllEventsAndTheirDetailsWithEntityGraph();

        return ResponseEntity.ok(events);
    }
}
