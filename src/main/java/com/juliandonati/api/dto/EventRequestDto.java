package com.juliandonati.api.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;
import java.util.Set;

@Data
@Schema(description = "Detalles de la solicitud para crear/actualizar un evento")
public class EventRequestDto {
    @Schema(description = "Nombre del evento")
    @NotBlank(message = "El nombre del evento no puede estar vacío.")
    private String name;

    @NotNull(message = "La fecha del evento no puede ser nula.")
    private LocalDate date;

    @NotBlank(message = "La ubicación del evento no se puede dejar en blanco.")
    private String location;

    @NotNull(message = "Debes definir una categoría para el evento.")
    private Long categoryId;

    @NotNull(message = "Debe haber por lo menos un speaker presente.")
    private Set<Long> speakerIds;
}
