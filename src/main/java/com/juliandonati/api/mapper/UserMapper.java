package com.juliandonati.api.mapper;

import com.juliandonati.api.domain.Role;
import com.juliandonati.api.domain.User;
import com.juliandonati.api.dto.UserResponseDto;
import com.juliandonati.api.exception.ResourceNotFoundException;
import com.juliandonati.api.security.dto.RegisterRequestDto;
import com.juliandonati.api.repository.RoleRepository;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public abstract class UserMapper {

    @Autowired
    protected RoleRepository roleRepository;

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", ignore = true) // La contraseña no la podemos pasar en el Dto, la tenemos que encriptar.

    @Mapping(target = "roles", source = "registerRequestDto.roles", qualifiedByName = "mapRoleStringsToRoles") // MUY IMPORTANTE

    @Mapping(target="attendedEvents", ignore = true)
    public abstract User toUser(RegisterRequestDto registerRequestDto);

    @Named("mapRoleStringsToRoles") // Definimos una etiqueta única para este metodo
    public Set<Role> mapRoleStringsToRoles(Set<String> roleNames) {
        if(roleNames == null || roleNames.isEmpty())
            return roleRepository.findByName("ROLE_USER")
                    .map(Collections::singleton)
                    .orElseThrow(()-> new ResourceNotFoundException("Error: El rol de USER no se encontró en la base de datos. " +
                            "ASEGÚRATE QUE EL ROL EXISTA AL INICIAR LA APLICACIÓN"));

        return roleNames.stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(()-> new ResourceNotFoundException("Error: El rol de " + roleName + " no se encontró en la base de datos. " +
                                "ASEGÚRATE QUE EL ROL EXISTA AL INICIAR LA APLICACIÓN")))
                .collect(Collectors.toSet());
    }
    
    public abstract UserResponseDto toUserResponseDto(User user);

    public abstract Set<UserResponseDto> toUserResponseDtoSet(Set<User> userSet);
}
