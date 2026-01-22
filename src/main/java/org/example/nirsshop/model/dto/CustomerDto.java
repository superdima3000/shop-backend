package org.example.nirsshop.model.dto;

public record CustomerDto(
        Integer id,
        String fullName,
        String address,
        String phone
) {}

