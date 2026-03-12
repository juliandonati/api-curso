package com.juliandonati.api.security.controller;

import com.juliandonati.api.domain.User;
import com.juliandonati.api.security.dto.JwtAuthResponseDto;
import com.juliandonati.api.security.dto.LoginDto;
import com.juliandonati.api.security.dto.RegisterRequestDto;
import com.juliandonati.api.mapper.UserMapper;
import com.juliandonati.api.repository.UserRepository;
import com.juliandonati.api.security.jwt.JwtGenerator;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;

    private final JwtGenerator jwtGenerator;

    private final UserRepository userRepository; // Tendría que ser el servicio.

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/login")
    public ResponseEntity<JwtAuthResponseDto> authenticateUser(@RequestBody LoginDto  loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword())
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);

        String token = jwtGenerator.generateToken(authentication);

        return new ResponseEntity<>(new JwtAuthResponseDto(token), HttpStatus.OK);
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody RegisterRequestDto registerRequestDto){
        if(userRepository.existsByUsername(registerRequestDto.getUsername()))
            return new ResponseEntity<>("El nombre de usuario está ocupado.",HttpStatus.BAD_REQUEST);

        if(userRepository.existsByEmail(registerRequestDto.getEmail()))
            return new ResponseEntity<>("El email ya lo ocupa una cuenta registrada.",HttpStatus.BAD_REQUEST);

        User user = userMapper.toUser(registerRequestDto);
        user.setPassword(passwordEncoder.encode(registerRequestDto.getPassword()));

        userRepository.save(user);

        return new ResponseEntity<>("¡Usuario registrado correctamente!",HttpStatus.CREATED);
    }
}
