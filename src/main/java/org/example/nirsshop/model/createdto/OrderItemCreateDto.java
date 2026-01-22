package org.example.nirsshop.model.createdto;

public record OrderItemCreateDto(
        Integer productId,
        Integer orderId,
        Integer quantity
) {}
