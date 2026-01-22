package org.example.nirsshop.model.dto;

public record TopIncomeProductDto (
        Integer productId,
        String name,
        Long totalIncome,
        Long totalSold
) {
}
