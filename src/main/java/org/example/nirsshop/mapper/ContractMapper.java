package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Contract;
import org.example.nirsshop.model.createdto.ContractCreateDto;
import org.example.nirsshop.model.dto.ContractDto;
import org.springframework.stereotype.Component;

@Component
public class ContractMapper implements Mapper<Contract, ContractDto, ContractCreateDto> {

    @Override
    public ContractDto toDto(Contract entity) {
        if (entity == null) return null;
        Integer employeeId = entity.getEmployee() != null ? entity.getEmployee().getEmployeeId() : null;
        return new ContractDto(
                entity.getContractId(),
                entity.getContractType(),
                entity.getSigningDate(),
                entity.getSalary(),
                employeeId
        );
    }

    @Override
    public Contract fromCreateDto(ContractCreateDto createDto) {
        if (createDto == null) return null;
        // employee задаётся в сервисе
        return Contract.builder()
                .contractType(createDto.contractType())
                .signingDate(createDto.signingDate())
                .salary(createDto.salary())
                .build();
    }
}
