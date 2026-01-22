package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.CustomerMapper;
import org.example.nirsshop.model.Customer;
import org.example.nirsshop.model.createdto.CustomerCreateDto;
import org.example.nirsshop.model.dto.CustomerDto;
import org.example.nirsshop.repository.CustomerRepository;
import org.example.nirsshop.service.CustomerService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CustomerServiceImpl implements CustomerService {

    private final CustomerRepository customerRepository;
    private final CustomerMapper customerMapper;

    @Override
    public List<CustomerDto> findAll() {
        return customerRepository.findAll()
                .stream()
                .map(customerMapper::toDto)
                .toList();
    }

    @Override
    public CustomerDto findById(Integer id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));
        return customerMapper.toDto(customer);
    }

    @Override
    public CustomerDto create(CustomerCreateDto createDto) {
        Customer customer = customerMapper.fromCreateDto(createDto);
        Customer saved = customerRepository.save(customer);
        return customerMapper.toDto(saved);
    }

    @Override
    public CustomerDto update(Integer id, CustomerCreateDto createDto) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Customer not found: " + id));

        customer.setFullName(createDto.fullName());
        customer.setAddress(createDto.address());
        customer.setPhone(createDto.phone());

        Customer saved = customerRepository.save(customer);
        return customerMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!customerRepository.existsById(id)) {
            throw new NotFoundException("Customer not found: " + id);
        }
        customerRepository.deleteById(id);
    }

    @Override
    public List<CustomerDto> searchByName(String query) {
        return customerRepository.findByFullNameContainingIgnoreCase(query)
                .stream()
                .map(customerMapper::toDto)
                .collect(Collectors.toList());
    }

}
