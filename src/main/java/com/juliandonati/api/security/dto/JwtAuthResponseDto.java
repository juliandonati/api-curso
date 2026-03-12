package com.juliandonati.api.security.dto;

import lombok.Data;

@Data
public class JwtAuthResponseDto {
    private String accessToken;
    private final String tokenType = "Bearer ";

    public JwtAuthResponseDto(String accessT) {
        accessToken = accessT;
    }
}
