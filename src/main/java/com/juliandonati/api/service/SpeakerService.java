package com.juliandonati.api.service;

import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.SpeakerRequestDto;

import java.util.List;

public interface SpeakerService {
    Speaker save(SpeakerRequestDto speakerRequestDto);
    Speaker updateSpeaker(SpeakerRequestDto speakerRequestDto, Long id);
    void deleteById(Long id);

    Speaker findById(Long id);
    List<Speaker> findAll();
}
