package org.example.nirsshop.model.createdto;

public record ProductStoreSizeCreateDto(
        Integer productId,
        Integer storeId,
        String sizeValue,
        Integer quantity
) {}
