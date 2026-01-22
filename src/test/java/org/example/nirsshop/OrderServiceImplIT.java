package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Customer;
import org.example.nirsshop.model.Order;
import org.example.nirsshop.model.createdto.OrderCreateDto;
import org.example.nirsshop.model.dto.OrderDto;
import org.example.nirsshop.repository.CustomerRepository;
import org.example.nirsshop.repository.OrderRepository;
import org.example.nirsshop.service.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class OrderServiceImplIT {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private Customer customer;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setAddress("Main Street 1");
        customer.setPhone("+123456789");
        customer = customerRepository.save(customer);
    }

    @Test
    void findAll_ReturnsAllOrders() {
        // Arrange
        Order order1 = createOrder(LocalDate.of(2024, 1, 1), "Pending", 5000, false);
        Order order2 = createOrder(LocalDate.of(2024, 1, 2), "Completed", 3000, true);
        orderRepository.saveAll(List.of(order1, order2));

        // Act
        List<OrderDto> result = orderService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingOrder_ReturnsOrderDto() {
        // Arrange
        Order order = createOrder(LocalDate.of(2024, 3, 1), "Processing", 7500, false);
        Order saved = orderRepository.save(order);

        // Act
        OrderDto result = orderService.findById(saved.getOrderId());

        // Assert
        assertNotNull(result);
        assertEquals(LocalDate.of(2024, 3, 1), result.orderDate());
        assertEquals("Processing", result.orderStatus());
        assertEquals(7500, result.totalAmount());
        assertFalse(result.isPaid());
        assertEquals(customer.getCustomerId(), result.customerId());
    }

    @Test
    void findById_NonExistingOrder_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void create_CreatesOrder() {
        // Arrange
        OrderCreateDto createDto = new OrderCreateDto(
                LocalDate.of(2024, 6, 15),
                "New",
                10000,
                1500,
                5,
                customer.getCustomerId(),
                false

        );

        // Act
        OrderDto result = orderService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals(LocalDate.of(2024, 6, 15), result.orderDate());
        assertEquals("New", result.orderStatus());
        assertEquals(10000, result.totalAmount());
        assertEquals(1500, result.weight());
        assertEquals(5, result.itemCount());
        assertFalse(result.isPaid());
        assertEquals(customer.getCustomerId(), result.customerId());

        Order savedOrder = orderRepository.findById(result.id()).orElseThrow();
        assertEquals("New", savedOrder.getOrderStatus());
        assertFalse(savedOrder.getIsPaid());
        assertEquals(customer.getCustomerId(), savedOrder.getCustomer().getCustomerId());
    }

    @Test
    void create_WithPaidOrder_CreatesOrder() {
        // Arrange
        OrderCreateDto createDto = new OrderCreateDto(
                LocalDate.of(2024, 6, 15),
                "Completed",
                10000,
                1500,
                5,
                customer.getCustomerId(),
                true

        );

        // Act
        OrderDto result = orderService.create(createDto);

        // Assert
        assertTrue(result.isPaid());
        Order savedOrder = orderRepository.findById(result.id()).orElseThrow();
        assertTrue(savedOrder.getIsPaid());
    }

    @Test
    void create_WithNonExistingCustomer_ThrowsNotFoundException() {
        // Arrange
        OrderCreateDto createDto = new OrderCreateDto(
                LocalDate.of(2024, 1, 1),
                "Pending",
                5000,
                500,
                3,
                9999,
                false

        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void update_ExistingOrder_UpdatesSuccessfully() {
        // Arrange
        Order order = createOrder(LocalDate.of(2024, 1, 1), "Pending", 5000, false);
        Order saved = orderRepository.save(order);

        OrderCreateDto updateDto = new OrderCreateDto(
                LocalDate.of(2024, 2, 1),
                "Shipped",
                6000,
                800,
                4,
                customer.getCustomerId(),
                true

        );

        // Act
        OrderDto result = orderService.update(saved.getOrderId(), updateDto);

        // Assert
        assertEquals(LocalDate.of(2024, 2, 1), result.orderDate());
        assertEquals("Shipped", result.orderStatus());
        assertEquals(6000, result.totalAmount());
        assertEquals(800, result.weight());
        assertEquals(4, result.itemCount());
        assertTrue(result.isPaid());
        assertEquals(customer.getCustomerId(), result.customerId());

        Order updatedOrder = orderRepository.findById(saved.getOrderId()).orElseThrow();
        assertEquals("Shipped", updatedOrder.getOrderStatus());
        assertEquals(6000, updatedOrder.getTotalAmount());
        assertTrue(updatedOrder.getIsPaid());
    }

    @Test
    void update_ChangeCustomer_UpdatesSuccessfully() {
        // Arrange
        Customer newCustomer = new Customer();
        newCustomer.setFullName("Jane Smith");
        newCustomer.setAddress("Second Street 2");
        newCustomer.setPhone("+987654321");
        newCustomer = customerRepository.save(newCustomer);

        Order order = createOrder(LocalDate.of(2024, 1, 1), "Pending", 5000, false);
        Order saved = orderRepository.save(order);

        OrderCreateDto updateDto = new OrderCreateDto(
                LocalDate.of(2024, 1, 1),
                "Pending",
                5000,
                500,
                3,
                newCustomer.getCustomerId(),
                false

        );

        // Act
        OrderDto result = orderService.update(saved.getOrderId(), updateDto);

        // Assert
        assertEquals(newCustomer.getCustomerId(), result.customerId());

        Order updatedOrder = orderRepository.findById(saved.getOrderId()).orElseThrow();
        assertEquals(newCustomer.getCustomerId(), updatedOrder.getCustomer().getCustomerId());
    }

    @Test
    void update_NonExistingOrder_ThrowsNotFoundException() {
        // Arrange
        OrderCreateDto updateDto = new OrderCreateDto(
                LocalDate.of(2024, 1, 1),
                "Pending",
                5000,
                500,
                3,
                customer.getCustomerId(),
                false

        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void update_WithNonExistingCustomer_ThrowsNotFoundException() {
        // Arrange
        Order order = createOrder(LocalDate.of(2024, 1, 1), "Pending", 5000, false);
        Order saved = orderRepository.save(order);

        OrderCreateDto updateDto = new OrderCreateDto(
                LocalDate.of(2024, 1, 1),
                "Pending",
                5000,
                500,
                3,
                9999,
                false

        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.update(saved.getOrderId(), updateDto)
        );
        assertTrue(exception.getMessage().contains("Customer not found"));
    }

    @Test
    void delete_ExistingOrder_DeletesSuccessfully() {
        // Arrange
        Order order = createOrder(LocalDate.of(2024, 1, 1), "To Delete", 5000, false);
        Order saved = orderRepository.save(order);
        Integer orderId = saved.getOrderId();

        // Act
        orderService.delete(orderId);

        // Assert
        assertFalse(orderRepository.existsById(orderId));
    }

    @Test
    void delete_NonExistingOrder_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> orderService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Order not found"));
    }

    private Order createOrder(LocalDate orderDate, String status, Integer totalAmount, Boolean isPaid) {
        Order order = new Order();
        order.setOrderDate(orderDate);
        order.setOrderStatus(status);
        order.setTotalAmount(totalAmount);
        order.setWeight(500);
        order.setItemCount(3);
        order.setIsPaid(isPaid);
        order.setCustomer(customer);
        return order;
    }
}
