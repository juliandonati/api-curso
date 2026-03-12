package com.juliandonati.api.service;

import com.juliandonati.api.domain.Category;
import com.juliandonati.api.domain.Event;
import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.mapper.EventMapper;
import com.juliandonati.api.repository.EventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;
    private final CategoryService categoryService;
    private final SpeakerService speakerService;


    @Override
    @Transactional(readOnly = true)
    public Page<EventResponseDto> findAll(String name, Pageable pageable) {
        Page<Event> eventsPage;

        if (name != null && !name.trim().isEmpty())
            eventsPage = eventRepository.findByNameContainingIgnoreCase(name, pageable);
        else
            eventsPage = eventRepository.findAll(pageable);

        List<EventResponseDto> eventDtos = eventsPage.getContent().stream()
                .map(eventMapper::toEventResponseDto)
                .toList();

        return new PageImpl<>(eventDtos, pageable, eventsPage.getTotalElements());
    }

    @Override
    @Transactional
    public Event save(EventRequestDto eventRequestDto) {
        Event event = eventMapper.toEntity(eventRequestDto);

        Category category = categoryService.findById(eventRequestDto.getCategoryId());
        event.setCategory(category);

        Set<Long> speakerIds = eventRequestDto.getSpeakerIds();
        if (speakerIds != null && !speakerIds.isEmpty())
            speakerIds.forEach(speakerId ->
                    event.addSpeaker(speakerService.findById(speakerId))
            );

        // OPINION PROPIA: Haberlo hecho en el Mapper hubiera sido mucho más legible, cumpliría la ley del bajo acoplamiento y sería más eficiente.

        return eventRepository.save(event);
    }

    @Override
    public Event update(EventRequestDto eventRequestDto, Long id) {
        Event eventToUpdate = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado el evento de id = " + id));
        eventMapper.updateEventFromDto(eventRequestDto, eventToUpdate);

        if (!eventRequestDto.getCategoryId().equals(eventToUpdate.getCategory().getId()))
            eventToUpdate.setCategory(categoryService.findById(eventRequestDto.getCategoryId()));

        Set<Long> speakerIds = eventRequestDto.getSpeakerIds();
        Set<Speaker> updatedSpeakers;
        if (speakerIds != null && !speakerIds.isEmpty()) {
            updatedSpeakers = speakerIds.stream().map(speakerService::findById).collect(Collectors.toSet());
            eventToUpdate.setSpeakers(updatedSpeakers);
        } else
            updatedSpeakers = new HashSet<>();

        Set<Speaker> speakersToUpdate = eventToUpdate.getSpeakers();

        // LO HACEMOS ASÍ PARA EVITAR LOS PROBLEMAS DE BIDIRECCIONALIDAD. ¡¡¡ES MUY IMPORTANTE!!!
        // ya que la lista de eventos del speaker debe actualizarse también.

        if (speakersToUpdate != null && !speakersToUpdate.isEmpty()) {
            speakersToUpdate.forEach(actualSpeaker -> {
                        if (!updatedSpeakers.contains(actualSpeaker))
                            eventToUpdate.removeSpeaker(actualSpeaker);
                    }
            );

            updatedSpeakers.forEach(actualSpeaker -> {
                        if (!speakersToUpdate.contains(actualSpeaker))
                            eventToUpdate.addSpeaker(actualSpeaker);
                    }
            );
        }

        // OPINION PROPIA: Haberlo hecho en el Mapper hubiera sido mucho más legible, cumpliría la ley del bajo acoplamiento y sería más eficiente.

        return eventRepository.save(eventToUpdate);
    }

    @Override
    @Transactional(readOnly = true)
    public Event findById(Long id) {
        return eventRepository.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Evento no encontrado (id respectiva = " + id + ')')
        );
    }

    @Override
    @Transactional
    public void deleteById(Long id) throws ResourceNotFoundException {
        Event eventToDelete = eventRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se ha encontrado un evento de id: " + id));
        eventRepository.delete(eventToDelete);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Event> getAllEventsAndTheirDetailsProblematic() {
        List<Event> events = eventRepository.findAll();

        events.forEach(event -> {
            event.getSpeakers().size();
            event.getSpeakers().stream().map(Speaker::getName).collect(Collectors.toSet());
            event.getCategory().getName();
            event.getAttendedUsers().size();
        });

        return events;
    }

    @Override
    public List<Event> getAllEventsAndTheirDetailsOptimizedWithJoinFetch() {
        List<Event> events = eventRepository.findAllWithCategoryAndSpeakers();

        events.forEach(event ->
                System.out.println("Event: " + event.getName() + " Category: " + event.getCategory().getName()
                + " Speakers: " + event.getSpeakers().stream().map(Speaker::getName).collect(Collectors.joining(", ")))
        );

        return events;
    }

    @Override
    public List<Event> getAllEventsAndTheirDetailsWithEntityGraph() {
        List<Event> events = eventRepository.findAllWithDetails();

        events.forEach(event ->
                System.out.println("Event: " + event.getName() + " Category: " + event.getCategory().getName()
                        + " Speakers: " + event.getSpeakers().stream().map(Speaker::getName).collect(Collectors.joining(", "))
                        + " Attended Users: " +  event.getAttendedUsers()
                )
        );

        return events;
    }


}
