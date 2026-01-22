package org.example.nirsshop.model.dto;

import java.math.BigDecimal;

public record StoreDto(
        Integer id,
        String address,
        String phone,
        BigDecimal rent,
        Double rating
) {}

