package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Customer;
import org.example.nirsshop.model.createdto.CustomerCreateDto;
import org.example.nirsshop.model.dto.CustomerDto;
import org.springframework.stereotype.Component;

@Component
public class CustomerMapper implements Mapper<Customer, CustomerDto, CustomerCreateDto> {

    @Override
    public CustomerDto toDto(Customer entity) {
        if (entity == null) return null;
        return new CustomerDto(
                entity.getCustomerId(),
                entity.getFullName(),
                entity.getAddress(),
                entity.getPhone()
        );
    }

    @Override
    public Customer fromCreateDto(CustomerCreateDto createDto) {
        if (createDto == null) return null;
        return Customer.builder()
                .address(createDto.address())
                .phone(createDto.phone())
                .fullName(createDto.fullName())
                .build();
    }
}

