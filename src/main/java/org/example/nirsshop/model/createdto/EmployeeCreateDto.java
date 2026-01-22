package org.example.nirsshop.model.createdto;

public record EmployeeCreateDto(
        String fullName,
        String phone,
        Integer storeId,
        Integer contractId
) {}

