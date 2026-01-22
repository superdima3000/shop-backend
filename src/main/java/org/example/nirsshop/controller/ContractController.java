package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.ContractCreateDto;
import org.example.nirsshop.model.dto.ContractDto;
import org.example.nirsshop.service.ContractService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/contracts")
@RequiredArgsConstructor
public class ContractController {

    private final ContractService contractService;

    @GetMapping
    public ResponseEntity<List<ContractDto>> getAllContracts() {
        List<ContractDto> contracts = contractService.findAll();
        return ResponseEntity.ok(contracts);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContractDto> getContractById(@PathVariable Integer id) {
        ContractDto contract = contractService.findById(id);
        return ResponseEntity.ok(contract);
    }

    @PostMapping
    public ResponseEntity<ContractDto> createContract(@RequestBody ContractCreateDto createDto) {
        ContractDto created = contractService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ContractDto> updateContract(
            @PathVariable Integer id,
            @RequestBody ContractCreateDto createDto) {
        ContractDto updated = contractService.update(id, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteContract(@PathVariable Integer id) {
        contractService.delete(id);
        return ResponseEntity.noContent().build();
    }
}

