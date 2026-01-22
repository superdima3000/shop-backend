package org.example.nirsshop.model.dto;

public record CategoryDto(
        Integer id,
        String name,
        Integer parentId
) {}

