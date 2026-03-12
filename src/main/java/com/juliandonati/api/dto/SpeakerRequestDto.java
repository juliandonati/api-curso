package com.juliandonati.api.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SpeakerRequestDto {
    @NotBlank(message = "El Speaker DEBE tener un nombre.")
    @Size(max = 50, message = "El nombre no puede exceder los 50 caracteres.")
    private String name;

    @Email(message = "El formato del E-mail ingresado es inválido.")
    @NotNull(message = "Debe ingresar un E-mail.")
    private String email;

    @NotBlank(message = "El Speaker debe tener una biografía.")
    @Size(max = 255, message = "La biografía no puede exceder los 255 caracteres.")
    private String bio;
}
