package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Contract;
import org.example.nirsshop.model.Employee;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.EmployeeCreateDto;
import org.example.nirsshop.model.dto.EmployeeDto;
import org.example.nirsshop.repository.ContractRepository;
import org.example.nirsshop.repository.EmployeeRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.EmployeeService;
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
class EmployeeServiceImplIT {

    @Autowired
    private EmployeeService employeeService;

    @Autowired
    private EmployeeRepository employeeRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ContractRepository contractRepository;

    private Store store;
    private Contract contract;

    @BeforeEach
    void setUp() {
        store = new Store();
        store.setAddress("Main Street 1");
        store.setPhone("+123456789");
        store.setRent(BigDecimal.valueOf(50000));
        store = storeRepository.save(store);

        contract = new Contract();
        contract.setContractType("Full-time");
        contract.setSigningDate(LocalDate.of(2024, 1, 1));
        contract.setSalary(BigDecimal.valueOf(60000));
        contract = contractRepository.save(contract);
    }

    @Test
    void findAll_ReturnsAllEmployees() {
        // Arrange
        Employee employee1 = createEmployee("John Doe", "+111111111");
        Employee employee2 = createEmployee("Jane Smith", "+222222222");
        employeeRepository.saveAll(List.of(employee1, employee2));

        // Act
        List<EmployeeDto> result = employeeService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingEmployee_ReturnsEmployeeDto() {
        // Arrange
        Employee employee = createEmployee("Alice Brown", "+333333333");
        Employee saved = employeeRepository.save(employee);

        // Act
        EmployeeDto result = employeeService.findById(saved.getEmployeeId());

        // Assert
        assertNotNull(result);
        assertEquals("Alice Brown", result.fullName());
        assertEquals("+333333333", result.phone());
        assertEquals(store.getStoreId(), result.storeId());
    }

    @Test
    void findById_NonExistingEmployee_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void create_WithoutContract_CreatesEmployee() {
        // Arrange
        EmployeeCreateDto createDto = new EmployeeCreateDto(
                "Bob Wilson",
                "+444444444",
                store.getStoreId(),
                null
        );

        // Act
        EmployeeDto result = employeeService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Bob Wilson", result.fullName());
        assertEquals("+444444444", result.phone());
        assertEquals(store.getStoreId(), result.storeId());
        assertNull(result.contractId());

        Employee savedEmployee = employeeRepository.findById(result.id()).orElseThrow();
        assertEquals("Bob Wilson", savedEmployee.getFullName());
        assertNull(savedEmployee.getContract());
    }

    @Test
    void create_WithContract_CreatesEmployee() {
        // Arrange
        EmployeeCreateDto createDto = new EmployeeCreateDto(
                "Carol Davis",
                "+555555555",
                store.getStoreId(),
                contract.getContractId()
        );

        // Act
        EmployeeDto result = employeeService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals("Carol Davis", result.fullName());
        assertEquals(store.getStoreId(), result.storeId());
        assertEquals(contract.getContractId(), result.contractId());

        Employee savedEmployee = employeeRepository.findById(result.id()).orElseThrow();
        assertEquals(contract.getContractId(), savedEmployee.getContract().getContractId());
    }

    @Test
    void create_WithNonExistingStore_ThrowsNotFoundException() {
        // Arrange
        EmployeeCreateDto createDto = new EmployeeCreateDto(
                "David Lee",
                "+666666666",
                9999,
                null
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    @Test
    void create_WithNonExistingContract_ThrowsNotFoundException() {
        // Arrange
        EmployeeCreateDto createDto = new EmployeeCreateDto(
                "Emma White",
                "+777777777",
                store.getStoreId(),
                9999
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Contract not found"));
    }

    @Test
    void update_ExistingEmployee_UpdatesSuccessfully() {
        // Arrange
        Employee employee = createEmployee("Old Name", "+888888888");
        Employee saved = employeeRepository.save(employee);

        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "New Name",
                "+999999999",
                store.getStoreId(),
                contract.getContractId()
        );

        // Act
        EmployeeDto result = employeeService.update(saved.getEmployeeId(), updateDto);

        // Assert
        assertEquals("New Name", result.fullName());
        assertEquals("+999999999", result.phone());
        assertEquals(store.getStoreId(), result.storeId());
        assertEquals(contract.getContractId(), result.contractId());

        Employee updatedEmployee = employeeRepository.findById(saved.getEmployeeId()).orElseThrow();
        assertEquals("New Name", updatedEmployee.getFullName());
        assertEquals("+999999999", updatedEmployee.getPhone());
    }

    @Test
    void update_RemoveContract_UpdatesSuccessfully() {
        // Arrange
        Employee employee = createEmployee("Frank Green", "+101010101");
        employee.setContract(contract);
        Employee saved = employeeRepository.save(employee);

        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "Frank Green",
                "+101010101",
                store.getStoreId(),
                null
        );

        // Act
        EmployeeDto result = employeeService.update(saved.getEmployeeId(), updateDto);

        // Assert
        assertNull(result.contractId());

        Employee updatedEmployee = employeeRepository.findById(saved.getEmployeeId()).orElseThrow();
        assertNull(updatedEmployee.getContract());
    }

    @Test
    void update_ChangeStore_UpdatesSuccessfully() {
        // Arrange
        Store newStore = new Store();
        newStore.setAddress("Second Street 2");
        newStore.setPhone("+987654321");
        newStore.setRent(BigDecimal.valueOf(40000));
        newStore = storeRepository.save(newStore);

        Employee employee = createEmployee("Grace Hall", "+121212121");
        Employee saved = employeeRepository.save(employee);

        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "Grace Hall",
                "+121212121",
                newStore.getStoreId(),
                null
        );

        // Act
        EmployeeDto result = employeeService.update(saved.getEmployeeId(), updateDto);

        // Assert
        assertEquals(newStore.getStoreId(), result.storeId());

        Employee updatedEmployee = employeeRepository.findById(saved.getEmployeeId()).orElseThrow();
        assertEquals(newStore.getStoreId(), updatedEmployee.getStore().getStoreId());
    }

    @Test
    void update_NonExistingEmployee_ThrowsNotFoundException() {
        // Arrange
        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "Name",
                "+000000000",
                store.getStoreId(),
                null
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    @Test
    void update_WithNonExistingStore_ThrowsNotFoundException() {
        // Arrange
        Employee employee = createEmployee("Henry King", "+131313131");
        Employee saved = employeeRepository.save(employee);

        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "Henry King",
                "+131313131",
                9999,
                null
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.update(saved.getEmployeeId(), updateDto)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    @Test
    void update_WithNonExistingContract_ThrowsNotFoundException() {
        // Arrange
        Employee employee = createEmployee("Ivy Martin", "+141414141");
        Employee saved = employeeRepository.save(employee);

        EmployeeCreateDto updateDto = new EmployeeCreateDto(
                "Ivy Martin",
                "+141414141",
                store.getStoreId(),
                9999
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.update(saved.getEmployeeId(), updateDto)
        );
        assertTrue(exception.getMessage().contains("Contract not found"));
    }

    @Test
    void delete_ExistingEmployee_DeletesSuccessfully() {
        // Arrange
        Employee employee = createEmployee("To Delete", "+151515151");
        Employee saved = employeeRepository.save(employee);
        Integer employeeId = saved.getEmployeeId();

        // Act
        employeeService.delete(employeeId);

        // Assert
        assertFalse(employeeRepository.existsById(employeeId));
    }

    @Test
    void delete_NonExistingEmployee_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> employeeService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Employee not found"));
    }

    private Employee createEmployee(String fullName, String phone) {
        Employee employee = new Employee();
        employee.setFullName(fullName);
        employee.setPhone(phone);
        employee.setStore(store);
        return employee;
    }
}

