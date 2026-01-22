package org.example.nirsshop.model.dto;

public record TopCleanIncomeProductDto (
        Integer productId,
        String name,
        Long cleanIncome,
        Long totalSold
) {
}
