package org.example.nirsshop.model.dto;

public record AuthResponseDto(
        String token,
        UserDto user
) {
}
