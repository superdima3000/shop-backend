package org.example.nirsshop.model.dto;

public record EmployeeDto(
        Integer id,
        String fullName,
        String phone,
        Integer storeId,
        Integer contractId
) {}

