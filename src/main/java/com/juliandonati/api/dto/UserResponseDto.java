package com.juliandonati.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDto {
    private Long id;
    private String name;
    private String username;
    private String email;
    private Set<RoleDto> roles;
    private Set<EventSummaryDto> attendedEvents;
}
