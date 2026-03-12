package com.juliandonati.api.service;

import com.juliandonati.api.domain.Speaker;
import com.juliandonati.api.dto.SpeakerRequestDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.mapper.SpeakerMapper;
import com.juliandonati.api.repository.SpeakerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SpeakerServiceImpl implements SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final SpeakerMapper speakerMapper;

    @Override
    @Transactional
    public Speaker save(SpeakerRequestDto speakerRequestDto) {
        return speakerRepository.save(speakerMapper.toEntity(speakerRequestDto));
    }

    @Override
    @Transactional
    public Speaker updateSpeaker(SpeakerRequestDto speakerRequestDto, Long id) {
        Speaker speakerToUpdate = speakerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró el speaker de id = " + id));

        speakerMapper.updateSpeakerFromRequestDto(speakerRequestDto, speakerToUpdate);

        return speakerRepository.save(speakerToUpdate);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        if (!speakerRepository.existsById(id))
            throw new ResourceNotFoundException("No se encontro el speaker de id = " + id);

        speakerRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Speaker findById(Long id) {
        return speakerRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("No se encontró el speaker de id = " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Speaker> findAll() {
        return speakerRepository.findAll();
    }
}
