package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.User;
import org.example.nirsshop.model.createdto.UserCreateDto;
import org.example.nirsshop.model.dto.AuthResponseDto;
import org.example.nirsshop.model.dto.LoginRequestDto;
import org.example.nirsshop.model.dto.UserDto;
import org.example.nirsshop.model.enums.Role;
import org.example.nirsshop.security.JwtTokenProvider;
import org.example.nirsshop.service.UserService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDto> register(@RequestBody UserCreateDto dto) {
        UserDto user = userService.create(dto);

        String token = jwtTokenProvider.generateToken(
                user.id().longValue(),
                user.email(),
                user.role(),
                user.firstName()
        );

        return ResponseEntity.ok(new AuthResponseDto(token, user));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@RequestBody LoginRequestDto request) {
        User userEntity = userService.getEntityByEmail(request.email());

        if (!passwordEncoder.matches(request.password(), userEntity.getPasswordHash())) {
            return ResponseEntity.status(401).build();
        }

        UserDto userDto = userService.findById(userEntity.getId());

        String token = jwtTokenProvider.generateToken(
                userEntity.getId().longValue(),
                userEntity.getEmail(),
                userEntity.getRole().name(),
                userEntity.getFirstName()
        );

        return ResponseEntity.ok(new AuthResponseDto(token, userDto));
    }
}
