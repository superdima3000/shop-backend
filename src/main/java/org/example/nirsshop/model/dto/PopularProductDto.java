package org.example.nirsshop.model.dto;

public record PopularProductDto(
        Integer productId,
        String name,
        String article,
        Integer price,
        Double rating,
        Long totalSold
) {}
