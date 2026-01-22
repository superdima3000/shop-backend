package org.example.nirsshop.model.dto;

import java.time.LocalDate;

public record OrderDto(
        Integer id,
        LocalDate orderDate,
        String orderStatus,
        Integer totalAmount,
        Integer weight,
        Integer itemCount,
        Integer customerId,
        Boolean isPaid
) {}

