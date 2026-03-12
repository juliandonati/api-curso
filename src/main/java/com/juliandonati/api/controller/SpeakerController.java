package com.juliandonati.api.controller;

import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.SpeakerRequestDto;
import com.juliandonati.api.dto.SpeakerResponseDto;
import com.juliandonati.api.mapper.SpeakerMapper;
import com.juliandonati.api.service.SpeakerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/speakers")
@RequiredArgsConstructor
public class SpeakerController {
    private final SpeakerService speakerService;
    private final SpeakerMapper speakerMapper;

    @PostMapping
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SpeakerResponseDto> createSpeaker(@Valid @RequestBody SpeakerRequestDto speakerRequestDto) {
        SpeakerResponseDto createdSpeakerResponseDto = speakerMapper.toResponseDto(speakerService.save(speakerRequestDto));

        return new ResponseEntity<>(createdSpeakerResponseDto, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    public ResponseEntity<SpeakerResponseDto> updateSpeaker(@Valid @RequestBody SpeakerRequestDto speakerRequestDto, @PathVariable Long id) {
        SpeakerResponseDto updatedSpeakerResponse = speakerMapper.toResponseDto(speakerService.updateSpeaker(speakerRequestDto, id));

        return ResponseEntity.ok(updatedSpeakerResponse);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<List<SpeakerResponseDto>> getAllSpeakers() {
        List<SpeakerResponseDto> speakerResponseDtoList = speakerMapper.toResponseDtoList(speakerService.findAll());

        return ResponseEntity.ok(speakerResponseDtoList);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','USER')")
    public ResponseEntity<SpeakerResponseDto> getSpeakerById(@PathVariable Long id) {
        Speaker searchedSpeaker = speakerService.findById(id);

        return ResponseEntity.ok(speakerMapper.toResponseDto(searchedSpeaker));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN')")
    ResponseEntity<Void> deleteSpeakerById(@PathVariable Long id) {
        speakerService.deleteById(id);

        return ResponseEntity.noContent().build();
    }
}
