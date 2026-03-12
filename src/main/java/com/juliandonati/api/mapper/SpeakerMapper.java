package com.juliandonati.api.mapper;

import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.SpeakerRequestDto;
import com.juliandonati.api.dto.SpeakerResponseDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SpeakerMapper {
    SpeakerResponseDto toResponseDto(Speaker speaker);

    @Mapping(target = "events", ignore = true)
    Speaker toEntity(SpeakerResponseDto speakerResponseDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "events", ignore = true)
    Speaker toEntity(SpeakerRequestDto speakerRequestDto);

    List<SpeakerResponseDto> toResponseDtoList(List<Speaker> speakers);

    @Mapping(target = "id", ignore = true)
    @Mapping(target="events", ignore = true)
    Speaker updateSpeakerFromRequestDto(SpeakerRequestDto speakerRequestDto, @MappingTarget Speaker speakerToUpdate);
}
