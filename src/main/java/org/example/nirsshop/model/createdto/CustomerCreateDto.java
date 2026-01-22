package org.example.nirsshop.model.createdto;

public record CustomerCreateDto(
        String fullName,
        String address,
        String phone
) {}

