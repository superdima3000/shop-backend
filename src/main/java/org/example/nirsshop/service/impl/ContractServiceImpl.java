package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ContractMapper;
import org.example.nirsshop.model.Contract;
import org.example.nirsshop.model.Employee;
import org.example.nirsshop.model.createdto.ContractCreateDto;
import org.example.nirsshop.model.dto.ContractDto;
import org.example.nirsshop.repository.ContractRepository;
import org.example.nirsshop.repository.EmployeeRepository;
import org.example.nirsshop.service.ContractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ContractServiceImpl implements ContractService {

    private final ContractRepository contractRepository;
    private final EmployeeRepository employeeRepository;
    private final ContractMapper contractMapper;

    @Override
    public List<ContractDto> findAll() {
        return contractRepository.findAll()
                .stream()
                .map(contractMapper::toDto)
                .toList();
    }

    @Override
    public ContractDto findById(Integer id) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found: " + id));
        return contractMapper.toDto(contract);
    }

    @Override
    public ContractDto create(ContractCreateDto createDto) {
        Contract contract = contractMapper.fromCreateDto(createDto);

        if (createDto.employeeId() != null) {
            Employee employee = employeeRepository.findById(createDto.employeeId())
                    .orElseThrow(() -> new NotFoundException("Employee not found: " + createDto.employeeId()));
            contract.setEmployee(employee);
        }

        Contract saved = contractRepository.save(contract);
        return contractMapper.toDto(saved);
    }

    @Override
    public ContractDto update(Integer id, ContractCreateDto createDto) {
        Contract contract = contractRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Contract not found: " + id));

        contract.setContractType(createDto.contractType());
        contract.setSigningDate(createDto.signingDate());
        contract.setSalary(createDto.salary());

        if (createDto.employeeId() != null) {
            Employee employee = employeeRepository.findById(createDto.employeeId())
                    .orElseThrow(() -> new NotFoundException("Employee not found: " + createDto.employeeId()));
            contract.setEmployee(employee);
        } else {
            contract.setEmployee(null);
        }

        Contract saved = contractRepository.save(contract);
        return contractMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!contractRepository.existsById(id)) {
            throw new NotFoundException("Contract not found: " + id);
        }
        contractRepository.deleteById(id);
    }
}

