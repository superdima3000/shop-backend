package org.example.nirsshop.model.createdto;

public record CategoryCreateDto(
        String name,
        Integer parentId
) {}

