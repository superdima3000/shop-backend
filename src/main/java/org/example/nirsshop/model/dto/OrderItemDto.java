package org.example.nirsshop.model.dto;

public record OrderItemDto(
        Integer productId,
        Integer orderId,
        Integer quantity
) {}

