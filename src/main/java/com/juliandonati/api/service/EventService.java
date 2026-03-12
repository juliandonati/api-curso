package com.juliandonati.api.service;

import com.juliandonati.api.domain.Event;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface EventService {
    Page<EventResponseDto> findAll(String name, Pageable pageable);
    Event save(EventRequestDto event);
    Event update(EventRequestDto event, Long id);
    Event findById(Long id);
    void deleteById(Long id) throws ResourceNotFoundException;
    List<Event> getAllEventsAndTheirDetailsProblematic();
    List<Event> getAllEventsAndTheirDetailsOptimizedWithJoinFetch();
    List<Event> getAllEventsAndTheirDetailsWithEntityGraph();
}
