package org.example.nirsshop.model.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractDto(
        Integer id,
        String contractType,
        LocalDate signingDate,
        BigDecimal salary,
        Integer employeeId
) {}
