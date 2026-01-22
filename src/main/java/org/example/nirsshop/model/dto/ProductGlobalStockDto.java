package org.example.nirsshop.model.dto;

public record ProductGlobalStockDto(
        Integer productId,
        Integer quantity,
        Double price
) {}

