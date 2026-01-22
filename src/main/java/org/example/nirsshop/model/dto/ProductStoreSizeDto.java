package org.example.nirsshop.model.dto;

public record ProductStoreSizeDto(
        Integer id,
        Integer productId,
        Integer storeId,
        String sizeValue,
        Integer quantity,
        String productName,  // Дополнительно
        String storeName     // Дополнительно
) {}
