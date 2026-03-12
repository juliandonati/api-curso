package com.juliandonati.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.Set;

@Data @NoArgsConstructor
@AllArgsConstructor
public class EventResponseDto {
    private Long id;
    private String name;
    private LocalDate date;
    private String location;

    // Por si aumenta el tamaño de Category en el futuro, y por consistencia usamos la id y el name solo...
    private Long categoryId;
    private String categoryName;

    private Set<SpeakerResponseDto> speakers;
}
