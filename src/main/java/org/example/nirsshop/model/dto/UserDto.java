package org.example.nirsshop.model.dto;

import org.example.nirsshop.model.enums.Role;

import java.time.LocalDateTime;

public record UserDto(
        Integer id,
        String email,
        String firstName,
        String lastName,
        String phone,
        LocalDateTime createdAt,
        String role
) {
}
