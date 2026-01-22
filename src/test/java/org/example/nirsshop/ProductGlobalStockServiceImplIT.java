package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductGlobalStock;
import org.example.nirsshop.model.createdto.ProductGlobalStockCreateDto;
import org.example.nirsshop.model.dto.ProductGlobalStockDto;
import org.example.nirsshop.repository.CategoryRepository;
import org.example.nirsshop.repository.ProductGlobalStockRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.service.ProductGlobalStockService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductGlobalStockServiceImplIT {

    @Autowired
    private ProductGlobalStockService productGlobalStockService;

    @Autowired
    private ProductGlobalStockRepository productGlobalStockRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product product1;
    private Product product2;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setName("Clothing");
        category = categoryRepository.save(category);

        product1 = createProduct("T-Shirt", "ART-001", 1000);
        product1 = productRepository.save(product1);

        product2 = createProduct("Jeans", "ART-002", 2000);
        product2 = productRepository.save(product2);
    }

    @Test
    void findAll_ReturnsAllGlobalStocks() {
        // Arrange
        ProductGlobalStock stock1 = createStock(product1, 100);
        System.out.println(stock1);
        ProductGlobalStock stock2 = createStock(product2, 50);
        System.out.println(stock2);
        productGlobalStockRepository.save(stock1);
        productGlobalStockRepository.save(stock2);

        // Act
        List<ProductGlobalStockDto> result = productGlobalStockService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingStock_ReturnsStockDto() {
        // Arrange
        ProductGlobalStock stock = createStock(product1, 150);
        ProductGlobalStock saved = productGlobalStockRepository.save(stock);

        // Act
        ProductGlobalStockDto result = productGlobalStockService.findById(saved.getProductId());

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(150, result.quantity());
    }

    @Test
    void findById_NonExistingStock_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productGlobalStockService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Product global stock not found"));
    }

    @Test
    void create_CreatesGlobalStock() {
        // Arrange
        ProductGlobalStockCreateDto createDto = new ProductGlobalStockCreateDto(
                product1.getProductId(),
                200
        );

        // Act
        ProductGlobalStockDto result = productGlobalStockService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(200, result.quantity());

        ProductGlobalStock savedStock = productGlobalStockRepository.findById(result.productId()).orElseThrow();
        assertEquals(200, savedStock.getQuantity());
        assertEquals(product1.getProductId(), savedStock.getProduct().getProductId());
    }

    @Test
    void create_WithZeroQuantity_CreatesGlobalStock() {
        // Arrange
        ProductGlobalStockCreateDto createDto = new ProductGlobalStockCreateDto(
                product2.getProductId(),
                0
        );

        // Act
        ProductGlobalStockDto result = productGlobalStockService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals(product2.getProductId(), result.productId());
        assertEquals(0, result.quantity());
    }

    @Test
    void create_WithNonExistingProduct_ThrowsNotFoundException() {
        // Arrange
        ProductGlobalStockCreateDto createDto = new ProductGlobalStockCreateDto(
                9999,
                100
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productGlobalStockService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void update_ExistingStock_UpdatesSuccessfully() {
        // Arrange
        ProductGlobalStock stock = createStock(product1, 100);
        ProductGlobalStock saved = productGlobalStockRepository.save(stock);

        ProductGlobalStockCreateDto updateDto = new ProductGlobalStockCreateDto(
                product1.getProductId(),
                250
        );

        // Act
        ProductGlobalStockDto result = productGlobalStockService.update(saved.getProductId(), updateDto);

        // Assert
        assertEquals(product1.getProductId(), result.productId());
        assertEquals(250, result.quantity());

        ProductGlobalStock updatedStock = productGlobalStockRepository.findById(saved.getProductId()).orElseThrow();
        assertEquals(250, updatedStock.getQuantity());
    }

    @Test
    void update_SetQuantityToZero_UpdatesSuccessfully() {
        // Arrange
        ProductGlobalStock stock = createStock(product1, 100);
        ProductGlobalStock saved = productGlobalStockRepository.save(stock);

        ProductGlobalStockCreateDto updateDto = new ProductGlobalStockCreateDto(
                product1.getProductId(),
                0
        );

        // Act
        ProductGlobalStockDto result = productGlobalStockService.update(saved.getProductId(), updateDto);

        // Assert
        assertEquals(0, result.quantity());

        ProductGlobalStock updatedStock = productGlobalStockRepository.findById(saved.getProductId()).orElseThrow();
        assertEquals(0, updatedStock.getQuantity());
    }

    @Test
    void update_NonExistingStock_ThrowsNotFoundException() {
        // Arrange
        ProductGlobalStockCreateDto updateDto = new ProductGlobalStockCreateDto(
                9999,
                100
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productGlobalStockService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Product global stock not found"));
    }

    @Test
    void delete_ExistingStock_DeletesSuccessfully() {
        // Arrange
        ProductGlobalStock stock = createStock(product1, 100);
        ProductGlobalStock saved = productGlobalStockRepository.save(stock);
        Integer stockId = saved.getProductId();

        // Act
        productGlobalStockService.delete(stockId);

        // Assert
        assertFalse(productGlobalStockRepository.existsById(stockId));
    }

    @Test
    void delete_NonExistingStock_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productGlobalStockService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Product global stock not found"));
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

    private ProductGlobalStock createStock(Product product, Integer quantity) {
        ProductGlobalStock stock = new ProductGlobalStock();
        stock.setProduct(product);
        stock.setQuantity(quantity);
        return stock;
    }
}

