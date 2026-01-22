package org.example.nirsshop.model.createdto;

import java.math.BigDecimal;

public record StoreCreateDto(
        String address,
        String phone,
        BigDecimal rent,
        Double rating
) {}

