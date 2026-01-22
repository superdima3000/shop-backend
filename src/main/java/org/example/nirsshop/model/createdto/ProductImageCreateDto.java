package org.example.nirsshop.model.createdto;

public record ProductImageCreateDto(
        Integer productId,
        String imageUrl,
        Boolean isPrimary,
        Integer displayOrder
) {}
