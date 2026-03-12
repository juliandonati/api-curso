package com.juliandonati.api.security.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterRequestDto {
    @NotBlank(message = "El nombre de usuario no puede dejarse en blanco.")
    @Size(min = 4, max = 20, message = "El nombre de usuario debe tener entre 4 y 20 caracteres.")
    private String username;

    @NotBlank(message = "El usuario debe tener una contraseña.")
    @Size(min = 4, message = "La contraseña debe tener 4 o más caracteres.")
    private String password;

    @Email(message = "Formato de e-mail inválido.")
    @NotBlank(message = "El usuario debe tener un e-mail asignado.")
    private String email;

    @NotBlank(message = "El nombre real del usuario no puede estar en blanco.")
    private String name;

    private Set<String> roles;
}
