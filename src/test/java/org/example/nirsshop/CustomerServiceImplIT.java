package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Customer;
import org.example.nirsshop.model.createdto.CustomerCreateDto;
import org.example.nirsshop.model.dto.CustomerDto;
import org.example.nirsshop.repository.CustomerRepository;
import org.example.nirsshop.service.CustomerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class CustomerServiceImplIT {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void findAll_ReturnsAllCustomers() {
        // Arrange
        Customer customer1 = createCustomer("John Doe", "Main Street 1", "+111111111");
        Customer customer2 = createCustomer("Jane Smith", "Second Street 2", "+222222222");
        customerRepository.saveAll(List.of(customer1, customer2));

        // Act
        List<CustomerDto> result = customerService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingCustomer_ReturnsCustomerDto() {
        // Arrange
        Customer customer = createCustomer("Alice Brown", "Third Avenue 3", "+333333333");
        Customer saved = customerRepository.save(customer);

        // Act
        CustomerDto result = customerService.findById(saved.getCustomerId());

        // Assert
        assertNotNull(result);
        assertEquals("Alice Brown", result.fullName());
        assertEquals("Third Avenue 3", result.address());
        assertEquals("+333333333", result.phone());
    }

    @Test
    void findById_NonExistingCustomer_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customerService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void create_CreatesCustomer() {
        // Arrange
        CustomerCreateDto createDto = new CustomerCreateDto(
                "Bob Wilson",
                "Fourth Street 4",
                "+444444444"
        );

        // Act
        CustomerDto result = customerService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Bob Wilson", result.fullName());
        assertEquals("Fourth Street 4", result.address());
        assertEquals("+444444444", result.phone());

        Customer savedCustomer = customerRepository.findById(result.id()).orElseThrow();
        assertEquals("Bob Wilson", savedCustomer.getFullName());
    }

    @Test
    void update_ExistingCustomer_UpdatesSuccessfully() {
        // Arrange
        Customer customer = createCustomer("Old Name", "Old Address", "+555555555");
        Customer saved = customerRepository.save(customer);

        CustomerCreateDto updateDto = new CustomerCreateDto(
                "New Name",
                "New Address",
                "+666666666"
        );

        // Act
        CustomerDto result = customerService.update(saved.getCustomerId(), updateDto);

        // Assert
        assertEquals("New Name", result.fullName());
        assertEquals("New Address", result.address());
        assertEquals("+666666666", result.phone());

        Customer updatedCustomer = customerRepository.findById(saved.getCustomerId()).orElseThrow();
        assertEquals("New Name", updatedCustomer.getFullName());
        assertEquals("New Address", updatedCustomer.getAddress());
    }

    @Test
    void update_NonExistingCustomer_ThrowsNotFoundException() {
        // Arrange
        CustomerCreateDto updateDto = new CustomerCreateDto(
                "Some Name",
                "Some Address",
                "+000000000"
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customerService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void delete_ExistingCustomer_DeletesSuccessfully() {
        // Arrange
        Customer customer = createCustomer("To Delete", "Delete Street", "+777777777");
        Customer saved = customerRepository.save(customer);
        Integer customerId = saved.getCustomerId();

        // Act
        customerService.delete(customerId);

        // Assert
        assertFalse(customerRepository.existsById(customerId));
    }

    @Test
    void delete_NonExistingCustomer_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> customerService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    private Customer createCustomer(String fullName, String address, String phone) {
        Customer customer = new Customer();
        customer.setFullName(fullName);
        customer.setAddress(address);
        customer.setPhone(phone);
        return customer;
    }
}

