package org.example.nirsshop.model.dto;

public record ProductStatsDto(
        Integer productId,
        String name,
        Double rating,
        Long cleanIncome,
        Long totalIncome,
        Long totalSold
) {
}
