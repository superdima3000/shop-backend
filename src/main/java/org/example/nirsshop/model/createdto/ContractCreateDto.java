package org.example.nirsshop.model.createdto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record ContractCreateDto(
        String contractType,
        LocalDate signingDate,
        BigDecimal salary,
        Integer employeeId
) {}

