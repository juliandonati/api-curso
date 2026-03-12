package com.juliandonati.api.controller;


import com.juliandonati.api.domain.Category;
import com.juliandonati.api.domain.Event;
import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.dto.SpeakerResponseDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.mapper.EventMapper;
import com.juliandonati.api.security.jwt.JwtAuthEntryPoint;
import com.juliandonati.api.security.jwt.JwtAuthenticatorFilter;
import com.juliandonati.api.security.jwt.JwtGenerator;
import com.juliandonati.api.service.EventService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.security.autoconfigure.SecurityAutoConfiguration;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(
        value = EventController.class,
        excludeAutoConfiguration =  {
                 SecurityAutoConfiguration.class,
                 UserDetailsServiceAutoConfiguration.class
            },
        excludeFilters = @ComponentScan.Filter(type = FilterType.ASSIGNABLE_TYPE, classes = {
                JwtAuthenticatorFilter.class,
                JwtGenerator.class,
                JwtAuthEntryPoint.class // También es un componente, causaría problemas
        })
)
class EventControllerTest {
    @Autowired
    private MockMvc mockMvc; // Sirve para simular peticiones Http

    private EventService eventService;

    private EventMapper eventMapper;

    @Autowired
    private ObjectMapper objectMapper; // Sirve para serializar y deserializar JSON

    private Category category;

    private Speaker speaker1, speaker2;

    private SpeakerResponseDto speakerResponseDto1, speakerResponseDto2;

    private Event eventEntity;
    private EventResponseDto eventResponseDto;

    @TestConfiguration
    static class EventControllerTestConfig{
        @Bean
        @Primary
        EventService eventService(){
            return mock(EventService.class);
        }

        @Bean
        @Primary
        EventMapper eventMapper(){
            return mock(EventMapper.class);
        }
    }

    @BeforeEach
    void setUp(@Autowired EventService eventServiceMock,
               @Autowired EventMapper eventMapperMock){
        eventService=eventServiceMock;
        eventMapper = eventMapperMock;

        reset(eventService, eventMapper);

        eventEntity = new Event();
        category = new Category(1L,"Categoría","Descripción de Categoría");
        speaker1 = new Speaker(1L,"John Doe","johndoe@example.com","Descripción de John Doe", Collections.singleton(eventEntity));
        speaker2 = new Speaker(2L,"Jane Doe","janedoe@example.com","Descripción de Jane Doe", Collections.singleton(eventEntity));

        eventEntity.setId(1L);
        eventEntity.setName("Conferencia para Tontos");
        eventEntity.setLocation("Buenos Aires");
        eventEntity.setDate(LocalDate.of(2024,12,10));
        eventEntity.setCategory(category);
        eventEntity.setSpeakers(new HashSet<>(Set.of(speaker1,speaker2)));

        speakerResponseDto1 = new SpeakerResponseDto();
        speakerResponseDto1.setId(speaker1.getId());
        speakerResponseDto1.setName(speaker1.getName());
        speakerResponseDto1.setEmail(speaker1.getEmail());
        speakerResponseDto1.setBio(speaker1.getBio());

        speakerResponseDto2 = new SpeakerResponseDto();
        speakerResponseDto2.setId(speaker2.getId());
        speakerResponseDto2.setName(speaker2.getName());
        speakerResponseDto2.setEmail(speaker2.getEmail());
        speakerResponseDto2.setBio(speaker2.getBio());

        eventResponseDto = new EventResponseDto();
        eventResponseDto.setId(eventEntity.getId());
        eventResponseDto.setName(eventEntity.getName());
        eventResponseDto.setLocation(eventEntity.getLocation());
        eventResponseDto.setDate(eventEntity.getDate());
        eventResponseDto.setCategoryId(category.getId());
        eventResponseDto.setSpeakers(new HashSet<>(Set.of(speakerResponseDto1,speakerResponseDto2)));
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} - Debe retornar un evento por ID cuando existe")
    @WithMockUser(username = "testUser", roles = "USER") // Simula la autenticación
    void shouldReturnEventById() throws Exception {
        // Preparación
        when(eventService.findById(anyLong())).thenReturn(eventEntity);
        when(eventMapper.toEventResponseDto(any(Event.class))).thenReturn(eventResponseDto);

        // Ejecución
        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{id}", 1L)
                .accept(MediaType.APPLICATION_JSON)
        )

        // Verificación

                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Conferencia para Tontos"))
                .andExpect(jsonPath("$.location").value("Buenos Aires"))
                .andExpect(jsonPath("$.date").value(LocalDate.of(2024,12,10).toString()))
                .andExpect(jsonPath("$.categoryId").value(1L))
                .andExpect(jsonPath("$.speakers.length()").value(2))
                .andExpect(jsonPath("$.speakers[2]").doesNotExist())

                .andExpect(jsonPath("$.speakers[?(@.name == 'John Doe')].name").value("John Doe"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'John Doe')].email").value("johndoe@example.com"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'John Doe')].bio").value("Descripción de John Doe"))

                .andExpect(jsonPath("$.speakers[?(@.name == 'Jane Doe')].name").value("Jane Doe"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Jane Doe')].email").value("janedoe@example.com"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Jane Doe')].bio").value("Descripción de Jane Doe"))
        ;

        verify(eventMapper, times(1)).toEventResponseDto(eventEntity);
        verify(eventService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET /api/v1/events/{id} - Debe retornar 404 Not Found cuando el evento no existe")
    @WithMockUser(username = "testUser", roles = "USER") // Simula la autenticación
    void shouldReturnNotFoundWhenEventsDoesNotExist() throws Exception {
        when(eventService.findById(anyLong())).thenThrow(
                new ResourceNotFoundException("Evento no encontrado con el id: 99")
        );



        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events/{id}", 99L)
                        .accept(MediaType.APPLICATION_JSON))



                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.message").value("Evento no encontrado con el id: 99"))
        ;
        verify(eventService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("GET /api/v1/events - Debe retornar todos los eventos paginados y filtrados")
    @WithMockUser(username = "testUser", roles = "USER") // Simula la autenticación
    void shouldReturnAllEventsPagedAndFiltered() throws Exception {
        SpeakerResponseDto speakerResponseDtoA = new SpeakerResponseDto(3L,"Juan Pérez","juanperez@example.com","Descripción de Juan Pérez");
        Set<SpeakerResponseDto> speakerResponseDtoSet = new HashSet<>(Set.of(speakerResponseDtoA));

        EventResponseDto eventResponseDto2 = new EventResponseDto();
        eventResponseDto2.setId(2L);
        eventResponseDto2.setName("Webinar de Spring Security");
        eventResponseDto2.setDate(LocalDate.of(2023,10,20));
        eventResponseDto2.setSpeakers(speakerResponseDtoSet);
        eventResponseDto2.setLocation("Buenos Aires");
        eventResponseDto2.setCategoryId(1L);
        eventResponseDto2.setCategoryName("Categoría");

        EventResponseDto eventResponseDto3 = new EventResponseDto();
        eventResponseDto3.setId(3L);
        eventResponseDto3.setName("Conferencia Cloud Nativo");
        eventResponseDto3.setDate(LocalDate.of(2025,10,20));
        eventResponseDto3.setSpeakers(speakerResponseDtoSet);
        eventResponseDto3.setLocation("Buenos Aires");
        eventResponseDto3.setCategoryId(1L);
        eventResponseDto3.setCategoryName("Categoría");

        List<EventResponseDto> eventResponseDtoList = new ArrayList<>(List.of(eventResponseDto2,eventResponseDto3));

        Pageable mockPageable = PageRequest.of(0, 10);

        Page<EventResponseDto> eventResponseDtoPage = new PageImpl<>(eventResponseDtoList, mockPageable, eventResponseDtoList.size());

        when(eventService.findAll(eq("Spring"),any(Pageable.class))).thenReturn(eventResponseDtoPage);



        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/events")
                .param("page", "0")
                .param("size", "10")
                .param("name","Spring")
                .accept(MediaType.APPLICATION_JSON)
        )



                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.content[0]").exists())
                .andExpect(jsonPath("$.content[1]").exists())
                .andExpect(jsonPath("$.content[2]").doesNotExist())

                .andExpect(jsonPath("$.content[0].id").value(2))
                .andExpect(jsonPath("$.content[0].name").value("Webinar de Spring Security"))
                .andExpect(jsonPath("$.content[0].date").value(LocalDate.of(2023,10,20).toString()))
                .andExpect(jsonPath("$.content[0].location").value("Buenos Aires"))
                .andExpect(jsonPath("$.content[0].categoryId").value(1L))
                .andExpect(jsonPath("$.content[0].categoryName").value("Categoría"))
                .andExpect(jsonPath("$.content[0].speakers.length()").value(1))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name == 'Juan Pérez')].id").value(3))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name == 'Juan Pérez')].name").value("Juan Pérez"))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name == 'Juan Pérez')].email").value("juanperez@example.com"))
                .andExpect(jsonPath("$.content[0].speakers[?(@.name == 'Juan Pérez')].bio").value("Descripción de Juan Pérez"))

                .andExpect(jsonPath("$.content[1].id").value(3))
                .andExpect(jsonPath("$.content[1].name").value("Conferencia Cloud Nativo"))
                .andExpect(jsonPath("$.content[1].date").value(LocalDate.of(2025,10,20).toString()))
                .andExpect(jsonPath("$.content[1].location").value("Buenos Aires"))
                .andExpect(jsonPath("$.content[1].categoryId").value(1L))
                .andExpect(jsonPath("$.content[1].categoryName").value("Categoría"))
                .andExpect(jsonPath("$.content[1].speakers.length()").value(1))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name == 'Juan Pérez')].id").value(3))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name == 'Juan Pérez')].name").value("Juan Pérez"))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name == 'Juan Pérez')].email").value("juanperez@example.com"))
                .andExpect(jsonPath("$.content[1].speakers[?(@.name == 'Juan Pérez')].bio").value("Descripción de Juan Pérez"))

                .andExpect(jsonPath("$.pageable.pageNumber").value(0))
                .andExpect(jsonPath("$.pageable.pageSize").value(10))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(1))
                .andExpect(jsonPath("$.last").value(true)) // ¿Estamos en la última página? = true
        ;

        verify(eventService, times(1)).findAll(eq("Spring"),any(Pageable.class));
        verify(eventService, never()).findById(anyLong()); // Explícito pero redundante. Es un 'TEST DEFENSIVO'.
    }


    @Test
    @DisplayName("POST /api/v1/events - Debe crear un evento y retornar 201 Created")
    @WithMockUser(username = "testAdmin", roles = "ADMIN") // Simula la autenticación
    void shouldCreateEventSuccesfully() throws Exception {
        // ARRANGE
        Event savedEvent = new Event();

        savedEvent.setId(2L);
        savedEvent.setName("Conferencia AWS");

        Category categoryA = new Category(2L,"Hosting en la Nube","Evento sobre como usar la nube para hostear aplicaciones");
        savedEvent.setCategory(categoryA);
        savedEvent.setLocation("Online");
        savedEvent.setDate(LocalDate.of(2024,1,20));

        Speaker speakerA = new Speaker(5L,"Pedro Pérez","pedroperez@example.com","Descripción de Pedro Pérez",Collections.singleton(savedEvent));
        savedEvent.setSpeakers(Collections.singleton(speakerA));


        EventRequestDto eventToBeCreatedRequestDto = new EventRequestDto();
        eventToBeCreatedRequestDto.setName(savedEvent.getName());
        eventToBeCreatedRequestDto.setLocation(savedEvent.getLocation());
        eventToBeCreatedRequestDto.setDate(savedEvent.getDate());
        eventToBeCreatedRequestDto.setCategoryId(savedEvent.getCategory().getId());
        eventToBeCreatedRequestDto.setSpeakerIds(Collections.singleton(speakerA.getId()));


        EventResponseDto createdEventResponseDto = new EventResponseDto();

        createdEventResponseDto.setId(savedEvent.getId());
        createdEventResponseDto.setName(savedEvent.getName());
        createdEventResponseDto.setLocation(savedEvent.getLocation());
        createdEventResponseDto.setDate(savedEvent.getDate());
        createdEventResponseDto.setCategoryId(savedEvent.getCategory().getId());
        createdEventResponseDto.setCategoryName(savedEvent.getCategory().getName());

        SpeakerResponseDto speakerResponseDtoA = new SpeakerResponseDto(speakerA.getId(),speakerA.getName(),speakerA.getEmail(),speakerA.getBio());
        createdEventResponseDto.setSpeakers(Collections.singleton(speakerResponseDtoA));


        when(eventService.save(eventToBeCreatedRequestDto)).thenReturn(savedEvent);
        when(eventMapper.toEventResponseDto(savedEvent)).thenReturn(createdEventResponseDto);


        // ACT
        mockMvc.perform(MockMvcRequestBuilders.post("/api/v1/events")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eventToBeCreatedRequestDto)))

        // ASSERT

                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))

                .andExpect(jsonPath("$.id").value(savedEvent.getId().intValue()))
                .andExpect(jsonPath("$.name").value(eventToBeCreatedRequestDto.getName()))
                .andExpect(jsonPath("$.location").value(eventToBeCreatedRequestDto.getLocation()))
                .andExpect(jsonPath("$.categoryId").value(categoryA.getId()))
                .andExpect(jsonPath("$.categoryName").value(categoryA.getName()))
                .andExpect(jsonPath("$.speakers.length()").value(1))

                .andExpect(jsonPath("$.speakers[?(@.name == 'Pedro Pérez')].name").value("Pedro Pérez"))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Pedro Pérez')].id").value( speakerA.getId().intValue()))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Pedro Pérez')].bio").value(speakerA.getBio()));

        verify(eventService, times(1)).save(any(EventRequestDto.class));
        verify(eventMapper, times(1)).toEventResponseDto(any(Event.class));
    }

    @Test
    @DisplayName("POST /api/v1/events/{id} - Debe actualizar un evento existente y retornar 200 OK")
    @WithMockUser(username = "testAdmin", roles = "ADMIN") // Simula la autenticación
    void shouldUpdateEventSuccessfully() throws Exception {
        // ARRANGE
        final Long eventId = 6L;
        Category eventCategory = new Category(98L,"Entretenimiento","Descripción de Entretenimiento");
        Event eventInDB = new Event();
        Speaker eventSpeaker = new Speaker(48L,"Javier Roberto","javierroberto@example.com","Descripción de Javier Roberto",Collections.singleton(eventInDB));

        eventInDB.setId(eventId);
        eventInDB.setName("Convención de ánime");
        eventInDB.setLocation("La Matanza");
        eventInDB.setDate(LocalDate.of(2024,1,23));
        eventInDB.setCategory(eventCategory);
        eventInDB.setSpeakers(Collections.singleton(eventSpeaker));

        EventRequestDto eventRequestDto =  new EventRequestDto();
        eventRequestDto.setName("Convención de ánime");
        eventRequestDto.setLocation("Entre Ríos");
        eventRequestDto.setDate(LocalDate.of(2024,1,24));
        eventRequestDto.setCategoryId(eventCategory.getId());
        eventRequestDto.setSpeakerIds(Collections.singleton(eventSpeaker.getId()));

        EventResponseDto updatedEventResponseDto = new EventResponseDto();
        updatedEventResponseDto.setId(eventId);
        updatedEventResponseDto.setName(eventRequestDto.getName());
        updatedEventResponseDto.setLocation(eventRequestDto.getLocation());
        updatedEventResponseDto.setDate(eventRequestDto.getDate());
        updatedEventResponseDto.setCategoryId(eventCategory.getId());
        updatedEventResponseDto.setCategoryName(eventCategory.getName());

        SpeakerResponseDto updatedSpeakerResponseDto = new SpeakerResponseDto(eventSpeaker.getId(),eventSpeaker.getName(),eventSpeaker.getEmail(),eventSpeaker.getBio());
        updatedEventResponseDto.setSpeakers(Collections.singleton(updatedSpeakerResponseDto));

        when(eventService.update(any(EventRequestDto.class),eq(eventId))).thenReturn(eventInDB);
        when(eventMapper.toEventResponseDto(any(Event.class))).thenReturn(updatedEventResponseDto);


        // ACT
        mockMvc.perform(MockMvcRequestBuilders.put("/api/v1/events/{id}", eventId)
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(eventRequestDto)))


        // ASSERT
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id").value(eventId.intValue()))
                .andExpect(jsonPath("$.name").value(eventRequestDto.getName()))
                .andExpect(jsonPath("$.location").value(eventRequestDto.getLocation()))
                .andExpect(jsonPath("$.categoryId").value(eventCategory.getId()))
                .andExpect(jsonPath("$.categoryName").value(eventCategory.getName()))
                .andExpect(jsonPath("$.speakers.length()").value(1))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Javier Roberto')].id").value(eventSpeaker.getId().intValue()))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Javier Roberto')].name").value(eventSpeaker.getName()))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Javier Roberto')].bio").value(eventSpeaker.getBio()))
                .andExpect(jsonPath("$.speakers[?(@.name == 'Javier Roberto')].email").value(eventSpeaker.getEmail()));

        verify(eventService, times(1)).update(any(EventRequestDto.class),eq(eventId));
        verify(eventMapper, times(1)).toEventResponseDto(any(Event.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/events/{id} - Debe eliminar un evento existente y retornar 204 NO CONTENT")
    @WithMockUser(username = "testAdmin", roles = "ADMIN") // Simula la autenticación
    void shouldSuccessfullyDeleteEvent() throws Exception {
        // ARRANGE
        final Long eventId = 92L;
        doNothing().when(eventService).deleteById(eventId);
        // ACT
        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/events/{id}",eventId))

        // ASSERT
                .andExpect(status().isNoContent());

        verify(eventService, times(1)).deleteById(eventId);
        verify(eventMapper, never()).toEventResponseDto(any(Event.class));
    }

    @Test
    @DisplayName("DELETE /api/v1/events/{id} - Debe intentar eliminar un evento no existente y retornar 404 NOT FOUND")
    @WithMockUser(username = "testAdmin", roles = "ADMIN") // Simula la autenticación
    void shouldThrowNotFoundWhenTryingToDeleteNonExistentEvent() throws Exception {
        final Long eventId = 99L;
        doThrow(new ResourceNotFoundException("No se encontró un evento de id: "+eventId)).when(eventService).deleteById(eventId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/api/v1/events/{id}",eventId))

                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status").value(404))
                .andExpect(jsonPath("$.error").value("Resource Not Found"))
                .andExpect(jsonPath("$.message").value("No se encontró un evento de id: "+eventId));

        verify(eventService, times(1)).deleteById(eventId);
        verify(eventMapper, never()).toEventResponseDto(any(Event.class));
    }
}