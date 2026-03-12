package com.juliandonati.api.service;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.domain.Event;
import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.mapper.EventMapper;
import com.juliandonati.api.repository.EventRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

// Esto vendría a ser un TEST UNITARIO

@ExtendWith(MockitoExtension.class)
public class EventServiceImplTest {
    @Mock
    private EventRepository eventRepository;
    @Mock
    private EventMapper eventMapper;
    @Mock
    private CategoryService categoryService;
    @Mock
    private SpeakerService speakerService;

    @InjectMocks
    private EventServiceImpl eventService;


    private Event event;
    private EventRequestDto eventRequestDto;
    private EventResponseDto eventResponseDto;
    private Category category;
    private Speaker speaker1;
    private Speaker speaker2;
    private Pageable pageable;


    // Se ejecuta antes de cada metodo test
    @BeforeEach
    void setUp(){
        category = new Category(1L,"Conferencia","Descripción de conferencia");
        speaker1 = new Speaker(10L,"John Doe","john@example.com","Bio de John", new HashSet<>());
        speaker2 = new Speaker(11L,"Jane Smith","jane@example.com","Bio de Jane", new HashSet<>());

        event = new Event();
        event.setId(1L);
        event.setName("Spring Boot Conf");
        event.setDate(LocalDate.of(2023,10,26));
        event.setLocation("Online");
        event.setCategory(category);
        event.getSpeakers().add(speaker1);
        event.getSpeakers().add(speaker2);

        eventRequestDto = new EventRequestDto();
        eventRequestDto.setName("SpringBootConf");
        eventRequestDto.setDate(LocalDate.of(2023,10,26));
        eventRequestDto.setLocation("Online");
        eventRequestDto.setCategoryId(1L);
        eventRequestDto.setSpeakerIds(new HashSet<>(Set.of(10L,11L)));

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(1L);
        eventResponseDto.setName("Spring Boot Conf");
        eventResponseDto.setDate(LocalDate.of(2023,10,26));
        eventResponseDto.setLocation("Online");
        eventResponseDto.setCategoryId(1L);
        eventResponseDto.setCategoryName("Conferencia");

        pageable = PageRequest.of(0, 10);
    }


    @Test
    @DisplayName("Debe retornar un Evento cuando el ID existe")
    void shouldReturnEventWhenIdExists(){
        // ARRANGE
        when(eventRepository.findById(anyLong())).thenReturn(Optional.of(event));

        // ACT
        Event foundEvent = eventService.findById(1L);

        // ASSERT
        assertNotNull(foundEvent);
        assertEquals(event.getId(),foundEvent.getId());
        verify(eventRepository, times(1)).findById(1L);
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException cuando el ID no existe")
    void shouldThrowResourceNotFoundExceptionWhenIdDoesNotExist(){
        // ARRANGE
        when(eventRepository.findById(anyLong())).thenReturn(Optional.empty());

        // ACT + ASSERT combinadas
        ResourceNotFoundException thrownException = assertThrows(
                ResourceNotFoundException.class,
                ()-> eventService.findById(99L));

        assertEquals("Evento no encontrado (id respectiva = 99)",thrownException.getMessage());

        verify(eventRepository, times(1)).findById(99L);
    }

    @Test
    @DisplayName("Debe guardar un Evento exitosamente con categoría y oradores")
    void shouldSaveEventSuccessfullyWithCategoryAndSpeakers(){
        // ARRANGE
        Event eventWithoudId = new Event();
        eventWithoudId.setName(eventRequestDto.getName());
        eventWithoudId.setDate(eventRequestDto.getDate());
        eventWithoudId.setLocation(eventRequestDto.getLocation());

        when(eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoudId);

        when(categoryService.findById(eventRequestDto.getCategoryId())).thenReturn(category);

        when(speakerService.findById(10L)).thenReturn(speaker1);
        when(speakerService.findById(11L)).thenReturn(speaker2);

        when(eventRepository.save(any(Event.class))).thenAnswer(
                invocation -> {
                    Event savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1L);
                    return savedEvent;
                }
        );

        // ACT
        Event savedEvent = eventService.save(eventRequestDto);

        // ASSERT
        assertNotNull(savedEvent);
        assertEquals(1L, savedEvent.getId());
        assertEquals(eventRequestDto.getName(), savedEvent.getName());
        assertEquals(eventRequestDto.getDate(), savedEvent.getDate());
        assertEquals(eventRequestDto.getLocation(), savedEvent.getLocation());
        assertEquals(category, savedEvent.getCategory());

        assertEquals(2,savedEvent.getSpeakers().size());
        assertTrue(savedEvent.getSpeakers().contains(speaker1));
        assertTrue(savedEvent.getSpeakers().contains(speaker2));

        verify(eventMapper,times(1)).toEntity(eventRequestDto);
        verify(categoryService, times(1)).findById(eventRequestDto.getCategoryId());
        verify(speakerService, times(1)).findById(10L);
        verify(speakerService, times(1)).findById(11L);

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe guardar un evento exitosamente sin oradores")
    void shouldSaveEventSuccessfullyWithoutSpeakers(){
        Event eventWithoudId = new Event();
        eventWithoudId.setName(eventRequestDto.getName());
        eventWithoudId.setDate(eventRequestDto.getDate());
        eventWithoudId.setLocation(eventRequestDto.getLocation());

        eventRequestDto.setSpeakerIds(new HashSet<>());

        when(eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoudId);

        when(categoryService.findById(eventRequestDto.getCategoryId())).thenReturn(category);

        when(eventRepository.save(any(Event.class))).thenAnswer(
                invocation -> {
                    Event savedEvent = invocation.getArgument(0);
                    savedEvent.setId(1L);
                    return savedEvent;
                }
        );



        Event savedEvent = eventService.save(eventRequestDto);
        assertNotNull(savedEvent);
        assertEquals(1L, savedEvent.getId());
        assertEquals(eventRequestDto.getName(), savedEvent.getName());
        assertEquals(eventRequestDto.getDate(), savedEvent.getDate());
        assertEquals(eventRequestDto.getLocation(), savedEvent.getLocation());
        assertEquals(category, savedEvent.getCategory());
        assertNotNull(savedEvent.getSpeakers());
        assertEquals(0, savedEvent.getSpeakers().size());
        assertTrue(savedEvent.getSpeakers().isEmpty());

        verify(eventMapper,times(1)).toEntity(eventRequestDto);
        verify(categoryService, times(1)).findById(eventRequestDto.getCategoryId());
        verify(speakerService, never()).findById(anyLong());

        verify(eventRepository, times(1)).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe lanzar ResourceNotFoundException si la categoría no existe al guardar")
    void shouldThrowResourceNotFoundExceptionWhenCategoryDoesNotExist(){
        Event eventWithoutId = new Event();

        when(eventMapper.toEntity(any(EventRequestDto.class))).thenReturn(eventWithoutId);

        when(categoryService.findById(anyLong())).thenThrow(new ResourceNotFoundException("No se ha logrado encontrar la categoría de id: " +
                eventRequestDto.getCategoryId()));

        when(speakerService.findById(10L)).thenReturn(speaker1);
        when(speakerService.findById(11L)).thenReturn(speaker2);

        ResourceNotFoundException thrownException = assertThrows(ResourceNotFoundException.class, () -> eventService.save(eventRequestDto));

        assertEquals("No se ha logrado encontrar la categoría de id: " + eventRequestDto.getCategoryId(), thrownException.getMessage());

        verify(eventRepository, never()).save(any(Event.class));
    }

    @Test
    @DisplayName("Debe retornar una página de eventos sin filtro de nombre")
    void shouldReturnPageOfEventsWithoutNameFilter(){
        List<Event> events = Collections.singletonList(event);
        Page<Event> eventPage = new PageImpl<>(events,pageable,1);

        when(eventRepository.findAll(pageable)).thenReturn(eventPage);

        when(eventMapper.toEventResponseDto(any(Event.class))).thenReturn(eventResponseDto);

        Page<EventResponseDto> result = eventService.findAll(null,pageable);


        assertNotNull(result);
        assertEquals(1,result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(eventResponseDto, result.getContent().getFirst());

        verify(eventMapper,times(1)).toEventResponseDto(event);
        verify(eventRepository,never()).findByNameContainingIgnoreCase(anyString(),any(Pageable.class));
        verify(eventRepository,times(1)).findAll(pageable);
    }

    @Test
    @DisplayName("Debe retornar una página de eventos con filtro de nombre")
    void shouldReturnPageOfEventsWithNameFilter(){
        List<Event> events = Collections.singletonList(event);
        Page<Event> eventPage = new PageImpl<>(events,pageable,1);
        String filterName = "Spring";

        when(eventRepository.findByNameContainingIgnoreCase(filterName,pageable)).thenReturn(eventPage);

        when(eventMapper.toEventResponseDto(any(Event.class))).thenReturn(eventResponseDto);

        Page<EventResponseDto> result = eventService.findAll(filterName,pageable);

        assertNotNull(result);
        assertEquals(1,result.getTotalElements());
        assertEquals(1, result.getContent().size());
        assertEquals(eventResponseDto, result.getContent().getFirst());

        verify(eventMapper,times(1)).toEventResponseDto(event);
        verify(eventRepository,times(1)).findByNameContainingIgnoreCase(anyString(),any(Pageable.class));
        verify(eventRepository,never()).findAll(any(Pageable.class));
    }
}