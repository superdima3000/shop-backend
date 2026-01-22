package org.example.nirsshop.model.createdto;

import java.time.LocalDate;

public record OrderCreateDto(
        LocalDate orderDate,
        String orderStatus,
        Integer totalAmount,
        Integer weight,
        Integer itemCount,
        Integer customerId,
        Boolean isPaid
) {}

