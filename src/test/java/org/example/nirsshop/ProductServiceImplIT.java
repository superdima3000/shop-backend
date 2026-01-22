package org.example.nirsshop;

import jakarta.persistence.EntityManager;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.*;
import org.example.nirsshop.model.createdto.ProductCreateDto;
import org.example.nirsshop.model.dto.PopularProductDto;
import org.example.nirsshop.model.dto.ProductDto;
import org.example.nirsshop.model.dto.StoreDto;
import org.example.nirsshop.repository.*;
import org.example.nirsshop.service.ProductService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductServiceImplIT {

    @Autowired
    private ProductService productService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Autowired
    private ProductStoreRepository productStoreRepository;

    @Autowired
    private EntityManager entityManager;

    private Category category;
    private Product product1;
    private Product product2;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Clothing");
        category = categoryRepository.save(category);

        product1 = createProduct("T-Shirt", "ART-001", 1000, 4.5);
        product1 = productRepository.save(product1);

        product2 = createProduct("Jeans", "ART-002", 2000, 3.8);
        product2 = productRepository.save(product2);
    }

    // ========== ТЕСТЫ ДЛЯ НОВЫХ МЕТОДОВ ==========

    @Autowired
    private ProductStoreSizeRepository productStoreSizeRepository;

    @Autowired
    private StoreRepository storeRepository;

    @Test
    void getQuantityInStore_ExistingProductAndStore_ReturnsCorrectQuantity() {
        // Arrange
        Product product = createProduct("Куртка", "ART-100", 5000);
        Product savedProduct = productRepository.save(product);

        Store store = new Store();
        store.setAddress("Москва, ул. Ленина, 1");
        store.setPhoneNumber("+79991234567");
        Store savedStore = storeRepository.save(store);

        ProductStoreSize size1 = new ProductStoreSize();
        size1.setProduct(savedProduct);
        size1.setStore(savedStore);
        size1.setSizeValue("48");
        size1.setQuantity(10);

        ProductStoreSize size2 = new ProductStoreSize();
        size2.setProduct(savedProduct);
        size2.setStore(savedStore);
        size2.setSizeValue("50");
        size2.setQuantity(5);

        productStoreSizeRepository.saveAll(List.of(size1, size2));

        // Act
        Integer quantity = productService.getQuantityInStore(savedProduct.getProductId(), savedStore.getStoreId());

        // Assert
        assertEquals(15, quantity); // 10 + 5
    }

    @Test
    void getQuantityInStore_NoQuantity_ReturnsZero() {
        // Arrange
        Product product = createProduct("Брюки", "ART-101", 3000);
        Product savedProduct = productRepository.save(product);

        Store store = new Store();
        store.setAddress("Санкт-Петербург, Невский пр., 10");
        store.setPhoneNumber("+79991234568");
        Store savedStore = storeRepository.save(store);

        // Act
        Integer quantity = productService.getQuantityInStore(savedProduct.getProductId(), savedStore.getStoreId());

        // Assert
        assertEquals(0, quantity);
    }

    @Test
    void isProductInStock_ProductAvailable_ReturnsTrue() {
        // Arrange
        Product product = createProduct("Рубашка", "ART-102", 2000);
        Product savedProduct = productRepository.save(product);

        Store store = new Store();
        store.setAddress("Казань, ул. Баумана, 5");
        store.setPhoneNumber("+79991234569");
        Store savedStore = storeRepository.save(store);

        ProductStoreSize size = new ProductStoreSize();
        size.setProduct(savedProduct);
        size.setStore(savedStore);
        size.setSizeValue("M");
        size.setQuantity(3);
        productStoreSizeRepository.save(size);

        // Act
        boolean inStock = productService.isProductInStock(savedProduct.getProductId(), savedStore.getStoreId());

        // Assert
        assertTrue(inStock);
    }

    @Test
    void isProductInStock_ProductNotAvailable_ReturnsFalse() {
        // Arrange
        Product product = createProduct("Свитер", "ART-103", 3500);
        Product savedProduct = productRepository.save(product);

        Store store = new Store();
        store.setAddress("Новосибирск, пр. Ленина, 20");
        store.setPhoneNumber("+79991234570");
        Store savedStore = storeRepository.save(store);

        // Act
        boolean inStock = productService.isProductInStock(savedProduct.getProductId(), savedStore.getStoreId());

        // Assert
        assertFalse(inStock);
    }

    @Test
    void searchByName_PartialMatch_ReturnsMatchingProducts() {
        // Arrange
        Product product1 = createProduct("Куртка кожаная", "ART-200", 8000);
        Product product2 = createProduct("Куртка зимняя", "ART-201", 6000);
        Product product3 = createProduct("Пальто зимнее", "ART-202", 10000);
        productRepository.saveAll(List.of(product1, product2, product3));

        // Act - ищем по неполному слову "куртк"
        List<ProductDto> results = productService.searchByName("куртк");

        // Assert
        assertEquals(2, results.size());
        assertTrue(results.stream().allMatch(p -> p.name().toLowerCase().contains("куртк")));
        assertTrue(results.stream().anyMatch(p -> p.name().equals("Куртка кожаная")));
        assertTrue(results.stream().anyMatch(p -> p.name().equals("Куртка зимняя")));
        assertFalse(results.stream().anyMatch(p -> p.name().equals("Пальто зимнее")));
    }

    @Test
    void searchByName_CaseInsensitive_ReturnsMatchingProducts() {
        // Arrange
        Product product = createProduct("ФУТБОЛКА Спортивная", "ART-203", 1500);
        productRepository.save(product);

        // Act - ищем в нижнем регистре
        List<ProductDto> results = productService.searchByName("футбол");

        // Assert
        assertFalse(results.isEmpty());
        assertTrue(results.stream().anyMatch(p -> p.name().toLowerCase().contains("футбол")));
    }

    @Test
    void searchByName_NoMatches_ReturnsEmptyList() {
        // Arrange
        Product product = createProduct("Джинсы", "ART-204", 3000);
        productRepository.save(product);

        // Act
        List<ProductDto> results = productService.searchByName("кроссовки");

        // Assert
        assertTrue(results.isEmpty());
    }

    @Test
    void getAvailableSizes_MultipleSizes_ReturnsDistinctSortedSizes() {
        // Arrange
        Product product = createProduct("Штаны", "ART-300", 4000);
        Product savedProduct = productRepository.save(product);

        Store store = new Store();
        store.setAddress("Екатеринбург, ул. 8 Марта, 15");
        store.setPhoneNumber("+79991234571");
        Store savedStore = storeRepository.save(store);

        ProductStoreSize size1 = new ProductStoreSize();
        size1.setProduct(savedProduct);
        size1.setStore(savedStore);
        size1.setSizeValue("48");
        size1.setQuantity(5);

        ProductStoreSize size2 = new ProductStoreSize();
        size2.setProduct(savedProduct);
        size2.setStore(savedStore);
        size2.setSizeValue("46");
        size2.setQuantity(3);

        ProductStoreSize size3 = new ProductStoreSize();
        size3.setProduct(savedProduct);
        size3.setStore(savedStore);
        size3.setSizeValue("50");
        size3.setQuantity(7);

        productStoreSizeRepository.saveAll(List.of(size1, size2, size3));

        // Act
        List<String> sizes = productService.getAvailableSizes(savedProduct.getProductId());

        // Assert
        assertEquals(3, sizes.size());
        assertEquals("46", sizes.get(0)); // Проверяем сортировку
        assertEquals("48", sizes.get(1));
        assertEquals("50", sizes.get(2));
    }

}
