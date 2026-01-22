package org.example.nirsshop.model.dto;

public record ProductImageDto(
        Integer id,
        String imageUrl,
        Boolean isPrimary,
        Integer displayOrder
) {}
