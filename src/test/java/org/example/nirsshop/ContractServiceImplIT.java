package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Contract;
import org.example.nirsshop.model.Employee;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.ContractCreateDto;
import org.example.nirsshop.model.dto.ContractDto;
import org.example.nirsshop.repository.ContractRepository;
import org.example.nirsshop.repository.EmployeeRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.ContractService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ContractServiceImplIT {

    @Autowired
    private ContractService contractService;

    @Autowired
    private ContractRepository contractRepository;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StoreRepository storeRepository;

    private Employee employee;
    private Store store;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setAddress("Main Street 1");
        store.setPhone("+123456789");
        store.setRent(BigDecimal.valueOf(50000));
        store = storeRepository.save(store);

        employee = new Employee();
        employee.setFullName("John Doe");
        employee.setPhone("+987654321");
        employee.setStore(store);
        employee = employeeRepository.save(employee);
    }

    @Test
    void findAll_ReturnsAllContracts() {
        // Arrange
        Contract contract1 = createContract("Full-time", LocalDate.of(2024, 1, 1), BigDecimal.valueOf(50000));
        Contract contract2 = createContract("Part-time", LocalDate.of(2024, 2, 1), BigDecimal.valueOf(30000));
        contractRepository.saveAll(List.of(contract1, contract2));

        // Act
        List<ContractDto> result = contractService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingContract_ReturnsContractDto() {
        // Arrange
        Contract contract = createContract("Full-time", LocalDate.of(2024, 1, 1), BigDecimal.valueOf(60000));
        Contract saved = contractRepository.save(contract);

        // Act
        ContractDto result = contractService.findById(saved.getContractId());

        // Assert
        assertNotNull(result);
        assertEquals("Full-time", result.contractType());
        assertEquals(LocalDate.of(2024, 1, 1), result.signingDate());
        assertEquals(BigDecimal.valueOf(60000), result.salary());
    }

    @Test
    void findById_NonExistingContract_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> contractService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Contract not found"));
    }

    @Test
    void create_WithoutEmployee_CreatesContract() {
        // Arrange
        ContractCreateDto createDto = new ContractCreateDto(
                "Temporary",
                LocalDate.of(2024, 6, 1),
                BigDecimal.valueOf(40000),
                null
        );

        // Act
        ContractDto result = contractService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Temporary", result.contractType());
        assertEquals(BigDecimal.valueOf(40000), result.salary());
        assertNull(result.employeeId());

        Contract savedContract = contractRepository.findById(result.id()).orElseThrow();
        assertEquals("Temporary", savedContract.getContractType());
        assertNull(savedContract.getEmployee());
    }

    @Test
    void create_WithEmployee_CreatesContract() {
        // Arrange
        ContractCreateDto createDto = new ContractCreateDto(
                "Full-time",
                LocalDate.of(2024, 3, 1),
                BigDecimal.valueOf(55000),
                employee.getEmployeeId()
        );

        // Act
        ContractDto result = contractService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals("Full-time", result.contractType());
        assertEquals(employee.getEmployeeId(), result.employeeId());

        Contract savedContract = contractRepository.findById(result.id()).orElseThrow();
        assertEquals(employee.getEmployeeId(), savedContract.getEmployee().getEmployeeId());
    }

    @Test
    void create_WithNonExistingEmployee_ThrowsNotFoundException() {
        // Arrange
        ContractCreateDto createDto = new ContractCreateDto(
                "Contract",
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(50000),
                9999
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> contractService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void update_ExistingContract_UpdatesSuccessfully() {
        // Arrange
        Contract contract = createContract("Old Type", LocalDate.of(2023, 1, 1), BigDecimal.valueOf(40000));
        Contract saved = contractRepository.save(contract);

        ContractCreateDto updateDto = new ContractCreateDto(
                "New Type",
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(70000),
                employee.getEmployeeId()
        );

        // Act
        ContractDto result = contractService.update(saved.getContractId(), updateDto);

        // Assert
        assertEquals("New Type", result.contractType());
        assertEquals(LocalDate.of(2024, 1, 1), result.signingDate());
        assertEquals(BigDecimal.valueOf(70000), result.salary());
        assertEquals(employee.getEmployeeId(), result.employeeId());

        Contract updatedContract = contractRepository.findById(saved.getContractId()).orElseThrow();
        assertEquals("New Type", updatedContract.getContractType());
        assertEquals(BigDecimal.valueOf(70000), updatedContract.getSalary());
    }

    @Test
    void update_RemoveEmployee_UpdatesSuccessfully() {
        // Arrange
        Contract contract = createContract("Contract", LocalDate.of(2024, 1, 1), BigDecimal.valueOf(50000));
        contract.setEmployee(employee);
        Contract saved = contractRepository.save(contract);

        ContractCreateDto updateDto = new ContractCreateDto(
                "Contract",
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(50000),
                null
        );

        // Act
        ContractDto result = contractService.update(saved.getContractId(), updateDto);

        // Assert
        assertNull(result.employeeId());

        Contract updatedContract = contractRepository.findById(saved.getContractId()).orElseThrow();
        assertNull(updatedContract.getEmployee());
    }

    @Test
    void update_NonExistingContract_ThrowsNotFoundException() {
        // Arrange
        ContractCreateDto updateDto = new ContractCreateDto(
                "Type",
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(50000),
                null
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> contractService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Contract not found"));
    }

    @Test
    void update_WithNonExistingEmployee_ThrowsNotFoundException() {
        // Arrange
        Contract contract = createContract("Contract", LocalDate.of(2024, 1, 1), BigDecimal.valueOf(50000));
        Contract saved = contractRepository.save(contract);

        ContractCreateDto updateDto = new ContractCreateDto(
                "Contract",
                LocalDate.of(2024, 1, 1),
                BigDecimal.valueOf(50000),
                9999
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> contractService.update(saved.getContractId(), updateDto)
        );
        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void delete_ExistingContract_DeletesSuccessfully() {
        // Arrange
        Contract contract = createContract("To Delete", LocalDate.of(2024, 1, 1), BigDecimal.valueOf(50000));
        Contract saved = contractRepository.save(contract);
        Integer contractId = saved.getContractId();

        // Act
        contractService.delete(contractId);

        // Assert
        assertFalse(contractRepository.existsById(contractId));
    }

    @Test
    void delete_NonExistingContract_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> contractService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Contract not found"));
    }

    private Contract createContract(String type, LocalDate signingDate, BigDecimal salary) {
        Contract contract = new Contract();
        contract.setContractType(type);
        contract.setSigningDate(signingDate);
        contract.setSalary(salary);
        return contract;
    }
}

