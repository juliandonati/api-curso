package com.juliandonati.api.mapper;

import com.juliandonati.api.domain.Role;
import com.juliandonati.api.dto.RoleDto;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface RoleMapper {
    RoleDto toDto(Role role);
    Role toEntity(RoleDto roleDto);
    Set<RoleDto> toDtoSet(Set<Role> roles);
}
