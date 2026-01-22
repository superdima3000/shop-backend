package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.CustomerCreateDto;
import org.example.nirsshop.model.dto.CustomerDto;

import java.util.List;

public interface CustomerService extends CrudService<CustomerDto, CustomerCreateDto, Integer> {
    List<CustomerDto> searchByName(String query);
}
