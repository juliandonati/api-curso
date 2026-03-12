package com.juliandonati.api.mapper;

import com.juliandonati.api.domain.Event;
import com.juliandonati.api.dto.EventRequestDto;
import com.juliandonati.api.dto.EventResponseDto;
import com.juliandonati.api.dto.EventSummaryDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface EventMapper {
    List<EventResponseDto> toEventResponseDtoList(List<Event> events);

    // Mapeo para la entrada
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "attendedUsers", ignore = true)
    Event toEntity(EventRequestDto eventRequestDto);


    // Mapeo para la salida
    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "categoryName", source = "category.name")
    EventResponseDto toEventResponseDto(Event event);

    // Metodo para actualizar una entidad existente.
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "speakers", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "attendedUsers", ignore = true)
    void updateEventFromDto(EventRequestDto eventRequestDto, @MappingTarget Event event);

    EventSummaryDto toEventSummaryDto(Event event);

    Set<EventSummaryDto> toEventSummaryDtoSet(Set<Event> eventSet);
}
