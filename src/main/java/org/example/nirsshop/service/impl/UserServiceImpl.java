package org.example.nirsshop.service.impl;

// UserServiceImpl
import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.ConflictException;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.UserMapper;
import org.example.nirsshop.model.User;
import org.example.nirsshop.model.createdto.UserCreateDto;
import org.example.nirsshop.model.dto.UserDto;
import org.example.nirsshop.model.enums.Role;
import org.example.nirsshop.repository.UserRepository;
import org.example.nirsshop.service.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toDto)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findById(Integer id) {
        User user = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + id));
        return userMapper.toDto(user);
    }


    @Override
    @Transactional(readOnly = true)
    public UserDto findByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() ->
                        new NotFoundException("Пользователь с email: " + email + " не найден."));
        return userMapper.toDto(user);
    }

    @Override
    @Transactional(readOnly = true)
    public User getEntityByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundException("Пользователь с email: " + email + " не найден."));
    }

    @Override
    public UserDto create(UserCreateDto createDto) {
        userRepository.findByEmail(createDto.email())
                .ifPresent(u -> {
                    throw new ConflictException("Пользователь с таким email уже зарегестрирован: " + createDto.email());
                });

        User user = userMapper.fromCreateDto(createDto);

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public UserDto update(Integer id, UserCreateDto createDto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() ->
                        new NotFoundException("User not found with id: " + id));

        if (!existing.getEmail().equals(createDto.email())) {
            userRepository.findByEmail(createDto.email())
                    .ifPresent(u -> {
                        throw new ConflictException("Пользователь с таким email уже зарегестрирован: " + createDto.email());
                    });
        }

        existing.setEmail(createDto.email());
        existing.setFirstName(createDto.firstName());
        existing.setLastName(createDto.lastName());
        existing.setPhone(createDto.phone());
        existing.setRole(Enum.valueOf(Role.class, createDto.role()));

        if (createDto.password() != null && !createDto.password().isBlank()) {
            String encoded = passwordEncoder.encode(createDto.password());
            existing.setPasswordHash(encoded);
        }

        User saved = userRepository.save(existing);
        return userMapper.toDto(saved);
    }


    @Override
    public void delete(Integer id) {
        if (!userRepository.existsById(id)) {
            throw new NotFoundException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
}

