package org.example.nirsshop.service.impl;

import jakarta.persistence.EntityManager;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.*;
import org.example.nirsshop.model.createdto.ProductStoreCreateDto;
import org.example.nirsshop.model.dto.ProductStoreDto;
import org.example.nirsshop.repository.CategoryRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.repository.ProductStoreRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.ProductStoreService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductStoreServiceImplIT {

    @Autowired
    private ProductStoreService productStoreService;

    @Autowired
    private ProductStoreRepository productStoreRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private EntityManager entityManager;

    private Product product1;
    private Product product2;
    private Store store1;
    private Store store2;
    private Category category;

    @BeforeEach
    void setUp() {
        store1 = new Store();
        store1.setAddress("Main Street 1");
        store1.setPhone("+123456789");
        store1.setRent(BigDecimal.valueOf(50000));
        store1 = storeRepository.save(store1);

        store2 = new Store();
        store2.setAddress("Second Street 2");
        store2.setPhone("+987654321");
        store2.setRent(BigDecimal.valueOf(40000));
        store2 = storeRepository.save(store2);

        category = new Category();
        category.setName("Clothing");
        category = categoryRepository.save(category);

        product1 = createProduct("T-Shirt", "ART-001", 1000);
        product1 = productRepository.save(product1);

        product2 = createProduct("Jeans", "ART-002", 2000);
        product2 = productRepository.save(product2);
    }

    @Test
    void findAll_ReturnsAllProductStores() {
        // Arrange
        ProductStore ps1 = createProductStore(product1, store1, 50);
        ProductStore ps2 = createProductStore(product2, store1, 30);

        entityManager.persist(ps1);
        entityManager.persist(ps2);
        entityManager.flush();

        // Act
        List<ProductStoreDto> result = productStoreService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingProductStore_ReturnsDto() {
        // Arrange
        ProductStore ps = createProductStore(product1, store1, 100);
        entityManager.persist(ps);
        entityManager.flush();

        // Act
        ProductStoreDto result = productStoreService.findById(product1.getProductId(), store1.getStoreId());

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(store1.getStoreId(), result.storeId());
        assertEquals(100, result.quantity());
    }

    @Test
    void findById_NonExistingProductStore_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productStoreService.findById(9999, 9999)
        );
        assertTrue(exception.getMessage().contains("Product store not found"));
    }

    @Test
    void create_CreatesProductStore() {
        // Arrange
        ProductStoreCreateDto createDto = new ProductStoreCreateDto(
                product1.getProductId(),
                store1.getStoreId(),
                75
        );

        // Act
        ProductStoreDto result = productStoreService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(store1.getStoreId(), result.storeId());
        assertEquals(75, result.quantity());

        ProductStore saved = productStoreRepository
                .findByProductIdAndStoreId(product1.getProductId(), store1.getStoreId())
                .orElseThrow();
        assertEquals(75, saved.getQuantity());
    }

    @Test
    void create_WithZeroQuantity_CreatesProductStore() {
        // Arrange
        ProductStoreCreateDto createDto = new ProductStoreCreateDto(
                product2.getProductId(),
                store2.getStoreId(),
                0
        );

        // Act
        ProductStoreDto result = productStoreService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(0, result.quantity());
    }

    @Test
    void create_WithNonExistingProduct_ThrowsNotFoundException() {
        // Arrange
        ProductStoreCreateDto createDto = new ProductStoreCreateDto(
                9999,
                store1.getStoreId(),
                50
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productStoreService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void create_WithNonExistingStore_ThrowsNotFoundException() {
        // Arrange
        ProductStoreCreateDto createDto = new ProductStoreCreateDto(
                product1.getProductId(),
                9999,
                50
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productStoreService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Store not found"));
    }

    @Test
    void update_ExistingProductStore_UpdatesSuccessfully() {
        // Arrange
        ProductStore ps = createProductStore(product1, store1, 50);
        entityManager.persist(ps);
        entityManager.flush();

        ProductStoreCreateDto updateDto = new ProductStoreCreateDto(
                product1.getProductId(),
                store1.getStoreId(),
                150
        );

        // Act
        ProductStoreDto result = productStoreService.update(
                product1.getProductId(),
                store1.getStoreId(),
                updateDto
        );

        // Assert
        assertEquals(150, result.quantity());

        ProductStore updated = productStoreRepository
                .findByProductIdAndStoreId(product1.getProductId(), store1.getStoreId())
                .orElseThrow();
        assertEquals(150, updated.getQuantity());
    }

    @Test
    void update_SetQuantityToZero_UpdatesSuccessfully() {
        // Arrange
        ProductStore ps = createProductStore(product1, store1, 50);
        entityManager.persist(ps);
        entityManager.flush();

        ProductStoreCreateDto updateDto = new ProductStoreCreateDto(
                product1.getProductId(),
                store1.getStoreId(),
                0
        );

        // Act
        ProductStoreDto result = productStoreService.update(
                product1.getProductId(),
                store1.getStoreId(),
                updateDto
        );

        // Assert
        assertEquals(0, result.quantity());
    }

    @Test
    void update_NonExistingProductStore_ThrowsNotFoundException() {
        // Arrange
        ProductStoreCreateDto updateDto = new ProductStoreCreateDto(
                9999,
                9999,
                100
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productStoreService.update(9999, 9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Product store not found"));
    }

    @Test
    void delete_ExistingProductStore_DeletesSuccessfully() {
        // Arrange
        ProductStore ps = createProductStore(product1, store1, 50);
        entityManager.persist(ps);
        entityManager.flush();

        // Act
        productStoreService.delete(product1.getProductId(), store1.getStoreId());

        // Assert
        assertFalse(productStoreRepository.existsByProductIdAndStoreId(
                product1.getProductId(),
                store1.getStoreId()
        ));
    }

    @Test
    void delete_NonExistingProductStore_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productStoreService.delete(9999, 9999)
        );
        assertTrue(exception.getMessage().contains("Product store not found"));
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

    private ProductStore createProductStore(Product product, Store store, Integer quantity) {
        ProductStore ps = new ProductStore();
        ProductStoreId id = new ProductStoreId();
        id.setProductId(product.getProductId());
        id.setStoreId(store.getStoreId());
        ps.setId(id);
        ps.setProduct(product);
        ps.setStore(store);
        ps.setQuantity(quantity);
        return ps;
    }
}
