package org.example.nirsshop;

import jakarta.persistence.EntityManager;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.*;
import org.example.nirsshop.model.createdto.OrderItemCreateDto;
import org.example.nirsshop.model.dto.OrderItemDto;
import org.example.nirsshop.repository.*;
import org.example.nirsshop.service.ProductOrderService;
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
class ProductOrderServiceImplIT {

    @Autowired
    private ProductOrderService productOrderService;

    @Autowired
    private ProductOrderRepository productOrderRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Product product1;
    private Product product2;
    private Order order1;
    private Customer customer;
    private Category category;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setFullName("John Doe");
        customer.setAddress("Main Street 1");
        customer.setPhone("+123456789");
        customer = customerRepository.save(customer);

        order1 = new Order();
        order1.setOrderDate(LocalDate.of(2024, 1, 1));
        order1.setOrderStatus("Pending");
        order1.setTotalAmount(5000);
        order1.setWeight(500);
        order1.setItemCount(2);
        order1.setCustomer(customer);
        order1 = orderRepository.save(order1);

        category = new Category();
        category.setName("Clothing");
        category = categoryRepository.save(category);

        product1 = createProduct("T-Shirt", "ART-001", 1000);
        product1 = productRepository.save(product1);

        product2 = createProduct("Jeans", "ART-002", 2000);
        product2 = productRepository.save(product2);
    }

    @Test
    void findAll_ReturnsAllProductOrders() {
        // Arrange
        ProductOrder po1 = createProductOrder(product1, order1, 2);
        ProductOrder po2 = createProductOrder(product2, order1, 3);

        entityManager.persist(po1);
        entityManager.persist(po2);
        entityManager.flush();

        // Act
        List<OrderItemDto> result = productOrderService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingProductOrder_ReturnsDto() {
        // Arrange
        ProductOrder po = createProductOrder(product1, order1, 5);
        entityManager.persist(po);
        entityManager.flush();

        // Act
        OrderItemDto result = productOrderService.findById(product1.getProductId(), order1.getOrderId());

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(order1.getOrderId(), result.orderId());
        assertEquals(5, result.quantity());
    }

    @Test
    void findById_NonExistingProductOrder_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productOrderService.findById(9999, 9999)
        );
        assertTrue(exception.getMessage().contains("Product order not found"));
    }

    @Test
    void create_CreatesProductOrder() {
        // Arrange
        OrderItemCreateDto createDto = new OrderItemCreateDto(
                product1.getProductId(),
                order1.getOrderId(),
                10
        );

        // Act
        OrderItemDto result = productOrderService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(order1.getOrderId(), result.orderId());
        assertEquals(10, result.quantity());

        ProductOrderId id = new ProductOrderId();
        id.setProductId(product1.getProductId());
        id.setOrderId(order1.getOrderId());
        ProductOrder saved = productOrderRepository.findById(id).orElseThrow();
        assertEquals(10, saved.getQuantity());
    }

    @Test
    void create_WithNonExistingProduct_ThrowsNotFoundException() {
        // Arrange
        OrderItemCreateDto createDto = new OrderItemCreateDto(
                9999,
                order1.getOrderId(),
                5
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productOrderService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void create_WithNonExistingOrder_ThrowsNotFoundException() {
        // Arrange
        OrderItemCreateDto createDto = new OrderItemCreateDto(
                product1.getProductId(),
                9999,
                5
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productOrderService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Order not found"));
    }

    @Test
    void update_ExistingProductOrder_UpdatesSuccessfully() {
        // Arrange
        ProductOrder po = createProductOrder(product1, order1, 5);
        entityManager.persist(po);
        entityManager.flush();

        OrderItemCreateDto updateDto = new OrderItemCreateDto(
                product1.getProductId(),
                order1.getOrderId(),
                15
        );

        // Act
        OrderItemDto result = productOrderService.update(
                product1.getProductId(),
                order1.getOrderId(),
                updateDto
        );

        // Assert
        assertEquals(15, result.quantity());

        ProductOrderId id = new ProductOrderId();
        id.setProductId(product1.getProductId());
        id.setOrderId(order1.getOrderId());
        ProductOrder updated = productOrderRepository.findById(id).orElseThrow();
        assertEquals(15, updated.getQuantity());
    }

    @Test
    void update_NonExistingProductOrder_ThrowsNotFoundException() {
        // Arrange
        OrderItemCreateDto updateDto = new OrderItemCreateDto(
                9999,
                9999,
                10
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productOrderService.update(9999, 9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Product order not found"));
    }

    @Test
    void delete_ExistingProductOrder_DeletesSuccessfully() {
        // Arrange
        ProductOrder po = createProductOrder(product1, order1, 5);
        entityManager.persist(po);
        entityManager.flush();

        ProductOrderId id = new ProductOrderId();
        id.setProductId(product1.getProductId());
        id.setOrderId(order1.getOrderId());

        // Act
        productOrderService.delete(product1.getProductId(), order1.getOrderId());

        // Assert
        assertFalse(productOrderRepository.existsById(id));
    }

    @Test
    void delete_NonExistingProductOrder_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productOrderService.delete(9999, 9999)
        );
        assertTrue(exception.getMessage().contains("Product order not found"));
    }

    private Product createProduct(String name, String article, Integer price) {
        Product product = new Product();
        product.setName(name);
        product.setArticle(article);
        product.setPrice(price);
        product.setWeight(200);
        product.setDescription("Test product");
        product.setGender("Male");
        product.setSize(42);
        product.setCategory(category);
        return product;
    }

    private ProductOrder createProductOrder(Product product, Order order, Integer quantity) {
        ProductOrder po = new ProductOrder();
        ProductOrderId id = new ProductOrderId();
        id.setProductId(product.getProductId());
        id.setOrderId(order.getOrderId());
        po.setId(id);
        po.setProduct(product);
        po.setOrder(order);
        po.setQuantity(quantity);
        return po;
    }
}
