package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Employee;
import org.example.nirsshop.model.createdto.EmployeeCreateDto;
import org.example.nirsshop.model.dto.EmployeeDto;
import org.springframework.stereotype.Component;

@Component
public class EmployeeMapper implements Mapper<Employee, EmployeeDto, EmployeeCreateDto> {

    @Override
    public EmployeeDto toDto(Employee entity) {
        if (entity == null) return null;
        Integer storeId = entity.getStore() != null ? entity.getStore().getStoreId() : null;
        Integer contractId = entity.getContract() != null ? entity.getContract().getContractId() : null;
        return new EmployeeDto(
                entity.getEmployeeId(),
                entity.getFullName(),
                entity.getPhone(),
                storeId,
                contractId
        );
    }

    @Override
    public Employee fromCreateDto(EmployeeCreateDto createDto) {
        if (createDto == null) return null;
        return Employee.builder()
                .phone(createDto.phone())
                .fullName(createDto.fullName())
                .build();
    }
}

