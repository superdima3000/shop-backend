package org.example.nirsshop.model.createdto;

public record ProductGlobalStockCreateDto(
        Integer productId,
        Integer quantity,
        Double price
) {}

