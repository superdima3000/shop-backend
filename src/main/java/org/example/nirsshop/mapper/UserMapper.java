package org.example.nirsshop.mapper;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.User;
import org.example.nirsshop.model.createdto.UserCreateDto;
import org.example.nirsshop.model.dto.UserDto;
import org.example.nirsshop.model.enums.Role;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserMapper implements Mapper<User, UserDto, UserCreateDto> {

    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDto toDto(User entity) {
        if (entity == null) {
            return null;
        }

        UserDto dto = new UserDto(
                entity.getId(),
                entity.getEmail(),
                entity.getFirstName(),
                entity.getLastName(),
                entity.getPhone(),
                entity.getCreatedAt(),
                entity.getRole().name()
        );

        return dto;
    }

    @Override
    public User fromCreateDto(UserCreateDto createDto) {
        if (createDto == null) {
            return null;
        }

        User user = new User();
        user.setEmail(createDto.email());
        user.setPasswordHash(passwordEncoder.encode(createDto.password())); // уже захэшированный пароль
        user.setFirstName(createDto.firstName());
        user.setLastName(createDto.lastName());
        user.setPhone(createDto.phone());
        user.setRole(Enum.valueOf(Role.class, createDto.role()));
        return user;
    }
}
