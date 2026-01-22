package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.EmployeeMapper;
import org.example.nirsshop.model.Contract;
import org.example.nirsshop.model.Employee;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.EmployeeCreateDto;
import org.example.nirsshop.model.dto.EmployeeDto;
import org.example.nirsshop.repository.ContractRepository;
import org.example.nirsshop.repository.EmployeeRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final StoreRepository storeRepository;
    private final ContractRepository contractRepository;
    private final EmployeeMapper employeeMapper;

    @Override
    public List<EmployeeDto> findAll() {
        return employeeRepository.findAll()
                .stream()
                .map(employeeMapper::toDto)
                .toList();
    }

    @Override
    public EmployeeDto findById(Integer id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + id));
        return employeeMapper.toDto(employee);
    }

    @Override
    public EmployeeDto create(EmployeeCreateDto createDto) {
        Employee employee = employeeMapper.fromCreateDto(createDto);

        Store store = storeRepository.findById(createDto.storeId())
                .orElseThrow(() -> new NotFoundException("Store not found: " + createDto.storeId()));
        employee.setStore(store);

        if (createDto.contractId() != null) {
            Contract contract = contractRepository.findById(createDto.contractId())
                    .orElseThrow(() -> new NotFoundException("Contract not found: " + createDto.contractId()));
            employee.setContract(contract);
        }

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    @Override
    public EmployeeDto update(Integer id, EmployeeCreateDto createDto) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Employee not found: " + id));

        employee.setFullName(createDto.fullName());
        employee.setPhone(createDto.phone());

        Store store = storeRepository.findById(createDto.storeId())
                .orElseThrow(() -> new NotFoundException("Store not found: " + createDto.storeId()));
        employee.setStore(store);

        if (createDto.contractId() != null) {
            Contract contract = contractRepository.findById(createDto.contractId())
                    .orElseThrow(() -> new NotFoundException("Contract not found: " + createDto.contractId()));
            employee.setContract(contract);
        } else {
            employee.setContract(null);
        }

        Employee saved = employeeRepository.save(employee);
        return employeeMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!employeeRepository.existsById(id)) {
            throw new NotFoundException("Employee not found: " + id);
        }
        employeeRepository.deleteById(id);
    }
}

