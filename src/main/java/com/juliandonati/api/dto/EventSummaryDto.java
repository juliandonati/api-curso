package com.juliandonati.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventSummaryDto {  // Resumen de EventResponseDto para evitar datos excesivos cuando es atributo de otra clase o para evitar bucles circulares.
    private Long id;
    private String name;
    private LocalDate date;
    private String location;
}
