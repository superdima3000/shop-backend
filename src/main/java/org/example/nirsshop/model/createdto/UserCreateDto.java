package org.example.nirsshop.model.createdto;

import java.time.LocalDateTime;

public record UserCreateDto (
        String email,
        String firstName,
        String lastName,
        String phone,
        String password,
        String role
) {
}
