package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductImage;
import org.example.nirsshop.model.createdto.ProductImageCreateDto;
import org.example.nirsshop.model.dto.ProductImageDto;
import org.example.nirsshop.repository.CategoryRepository;
import org.example.nirsshop.repository.ProductImageRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.service.ProductImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
class ProductImageServiceImplIT {

    @Autowired
    private ProductImageService productImageService;

    @Autowired
    private ProductImageRepository productImageRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    private Product product;

    @BeforeEach
    void setUp() {
        Category category = new Category();
        category.setName("Test Category");
        category = categoryRepository.save(category);

        product = new Product();
        product.setName("Test Product");
        product.setArticle("TEST-001");
        product.setPrice(1000);
        product.setWeight(200);
        product.setDescription("Test description");
        product.setGender("Male");
        product.setSize(42);
        product.setRating(4.5);
        product.setCategory(category);
        product = productRepository.save(product);
    }

    @Test
    void findAll_ReturnsAllImages() {
        // Arrange
        ProductImage image1 = createImage("https://example.com/1.jpg", true, 0);
        ProductImage image2 = createImage("https://example.com/2.jpg", false, 1);
        productImageRepository.saveAll(List.of(image1, image2));

        // Act
        List<ProductImageDto> result = productImageService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingImage_ReturnsImageDto() {
        // Arrange
        ProductImage image = createImage("https://example.com/test.jpg", true, 0);
        ProductImage saved = productImageRepository.save(image);

        // Act
        ProductImageDto result = productImageService.findById(saved.getImageId());

        // Assert
        assertNotNull(result);
        assertEquals("https://example.com/test.jpg", result.imageUrl());
        assertTrue(result.isPrimary());
        assertEquals(0, result.displayOrder());
    }

    @Test
    void findById_NonExistingImage_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Product image not found"));
    }

    @Test
    void findByProductId_ReturnsImagesOrderedByDisplayOrder() {
        // Arrange
        ProductImage image1 = createImage("https://example.com/1.jpg", false, 2);
        ProductImage image2 = createImage("https://example.com/2.jpg", true, 0);
        ProductImage image3 = createImage("https://example.com/3.jpg", false, 1);
        productImageRepository.saveAll(List.of(image1, image2, image3));

        // Act
        List<ProductImageDto> result = productImageService.findByProductId(product.getProductId());

        // Assert
        assertEquals(3, result.size());
        assertEquals(0, result.get(0).displayOrder());
        assertEquals(1, result.get(1).displayOrder());
        assertEquals(2, result.get(2).displayOrder());
        assertEquals("https://example.com/2.jpg", result.get(0).imageUrl());
    }

    @Test
    void create_FirstImage_AutomaticallySetsPrimary() {
        // Arrange
        ProductImageCreateDto createDto = new ProductImageCreateDto(
                product.getProductId(),
                "https://example.com/first.jpg",
                false,  // не указываем как главную
                0
        );

        // Act
        ProductImageDto result = productImageService.create(createDto);

        // Assert
        assertNotNull(result);
        assertTrue(result.isPrimary());  // должна стать главной автоматически
        assertEquals("https://example.com/first.jpg", result.imageUrl());
    }

    @Test
    void create_WithPrimaryTrue_SetsAsPrimaryAndRemovesOtherPrimary() {
        // Arrange
        ProductImage existingPrimary = createImage("https://example.com/old-primary.jpg", true, 0);
        productImageRepository.save(existingPrimary);

        ProductImageCreateDto createDto = new ProductImageCreateDto(
                product.getProductId(),
                "https://example.com/new-primary.jpg",
                true,
                1
        );

        // Act
        ProductImageDto result = productImageService.create(createDto);

        // Assert
        assertTrue(result.isPrimary());

        // Проверяем, что старая главная картинка больше не главная
        ProductImage oldImage = productImageRepository.findById(existingPrimary.getImageId()).orElseThrow();
        assertFalse(oldImage.getIsPrimary());
    }

    @Test
    void create_WithNonExistingProduct_ThrowsNotFoundException() {
        // Arrange
        ProductImageCreateDto createDto = new ProductImageCreateDto(
                9999,
                "https://example.com/test.jpg",
                false,
                0
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void update_ExistingImage_UpdatesSuccessfully() {
        // Arrange
        ProductImage image = createImage("https://example.com/old.jpg", false, 0);
        ProductImage saved = productImageRepository.save(image);

        ProductImageCreateDto updateDto = new ProductImageCreateDto(
                product.getProductId(),
                "https://example.com/new.jpg",
                false,
                5
        );

        // Act
        ProductImageDto result = productImageService.update(saved.getImageId(), updateDto);

        // Assert
        assertEquals("https://example.com/new.jpg", result.imageUrl());
        assertEquals(5, result.displayOrder());

        ProductImage updated = productImageRepository.findById(saved.getImageId()).orElseThrow();
        assertEquals("https://example.com/new.jpg", updated.getImageUrl());
    }

    @Test
    void update_SetPrimaryTrue_RemovesOtherPrimary() {
        // Arrange
        ProductImage primaryImage = createImage("https://example.com/primary.jpg", true, 0);
        ProductImage secondaryImage = createImage("https://example.com/secondary.jpg", false, 1);
        productImageRepository.saveAll(List.of(primaryImage, secondaryImage));

        ProductImageCreateDto updateDto = new ProductImageCreateDto(
                product.getProductId(),
                "https://example.com/secondary.jpg",
                true,  // делаем главной
                1
        );

        // Act
        ProductImageDto result = productImageService.update(secondaryImage.getImageId(), updateDto);

        // Assert
        assertTrue(result.isPrimary());

        // Старая главная картинка больше не главная
        ProductImage oldPrimary = productImageRepository.findById(primaryImage.getImageId()).orElseThrow();
        assertFalse(oldPrimary.getIsPrimary());
    }

    @Test
    void update_NonExistingImage_ThrowsNotFoundException() {
        // Arrange
        ProductImageCreateDto updateDto = new ProductImageCreateDto(
                product.getProductId(),
                "https://example.com/test.jpg",
                false,
                0
        );

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Product image not found"));
    }

    @Test
    void delete_ExistingImage_DeletesSuccessfully() {
        // Arrange
        ProductImage image = createImage("https://example.com/to-delete.jpg", false, 0);
        ProductImage saved = productImageRepository.save(image);
        Integer imageId = saved.getImageId();

        // Act
        productImageService.delete(imageId);

        // Assert
        assertFalse(productImageRepository.existsById(imageId));
    }

    @Test
    void delete_PrimaryImage_MakesNextImagePrimary() {
        // Arrange
        ProductImage primaryImage = createImage("https://example.com/primary.jpg", true, 0);
        ProductImage secondImage = createImage("https://example.com/second.jpg", false, 1);
        productImageRepository.saveAll(List.of(primaryImage, secondImage));

        // Act
        productImageService.delete(primaryImage.getImageId());

        // Assert
        assertFalse(productImageRepository.existsById(primaryImage.getImageId()));

        // Вторая картинка должна стать главной
        ProductImage newPrimary = productImageRepository.findById(secondImage.getImageId()).orElseThrow();
        assertTrue(newPrimary.getIsPrimary());
    }

    @Test
    void delete_NonExistingImage_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Product image not found"));
    }

    @Test
    void setPrimaryImage_SetsImageAsPrimary() {
        // Arrange
        ProductImage image1 = createImage("https://example.com/1.jpg", true, 0);
        ProductImage image2 = createImage("https://example.com/2.jpg", false, 1);
        productImageRepository.saveAll(List.of(image1, image2));

        // Act
        ProductImageDto result = productImageService.setPrimaryImage(
                product.getProductId(),
                image2.getImageId()
        );

        // Assert
        assertTrue(result.isPrimary());

        // Первая картинка больше не главная
        ProductImage oldPrimary = productImageRepository.findById(image1.getImageId()).orElseThrow();
        assertFalse(oldPrimary.getIsPrimary());

        // Вторая картинка теперь главная
        ProductImage newPrimary = productImageRepository.findById(image2.getImageId()).orElseThrow();
        assertTrue(newPrimary.getIsPrimary());
    }

    @Test
    void setPrimaryImage_WithNonExistingProduct_ThrowsNotFoundException() {
        // Arrange
        ProductImage image = createImage("https://example.com/test.jpg", false, 0);
        ProductImage saved = productImageRepository.save(image);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.setPrimaryImage(9999, saved.getImageId())
        );
        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    void setPrimaryImage_WithNonExistingImage_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.setPrimaryImage(product.getProductId(), 9999)
        );
        assertTrue(exception.getMessage().contains("Product image not found"));
    }

    @Test
    void setPrimaryImage_WithImageFromDifferentProduct_ThrowsException() {
        // Arrange
        Product anotherProduct = new Product();
        anotherProduct.setName("Another Product");
        anotherProduct.setArticle("TEST-002");
        anotherProduct.setPrice(2000);
        anotherProduct = productRepository.save(anotherProduct);

        ProductImage imageFromAnotherProduct = new ProductImage();
        imageFromAnotherProduct.setProduct(anotherProduct);
        imageFromAnotherProduct.setImageUrl("https://example.com/other.jpg");
        imageFromAnotherProduct.setIsPrimary(false);
        imageFromAnotherProduct.setDisplayOrder(0);
        imageFromAnotherProduct = productImageRepository.save(imageFromAnotherProduct);

        Integer wrongImageId = imageFromAnotherProduct.getImageId();

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productImageService.setPrimaryImage(product.getProductId(), wrongImageId)
        );
        assertTrue(exception.getMessage().contains("does not belong to this product"));
    }

    @Test
    void reorderImages_ChangesDisplayOrder() {
        // Arrange
        ProductImage image1 = createImage("https://example.com/1.jpg", false, 0);
        ProductImage image2 = createImage("https://example.com/2.jpg", false, 1);
        ProductImage image3 = createImage("https://example.com/3.jpg", false, 2);
        productImageRepository.saveAll(List.of(image1, image2, image3));

        List<Integer> newOrder = List.of(
                image3.getImageId(),
                image1.getImageId(),
                image2.getImageId()
        );

        // Act
        productImageService.reorderImages(product.getProductId(), newOrder);

        // Assert
        ProductImage reordered1 = productImageRepository.findById(image3.getImageId()).orElseThrow();
        ProductImage reordered2 = productImageRepository.findById(image1.getImageId()).orElseThrow();
        ProductImage reordered3 = productImageRepository.findById(image2.getImageId()).orElseThrow();

        assertEquals(0, reordered1.getDisplayOrder());
        assertEquals(1, reordered2.getDisplayOrder());
        assertEquals(2, reordered3.getDisplayOrder());
    }

    @Test
    void reorderImages_WithNonExistingImage_ThrowsNotFoundException() {
        // Arrange
        List<Integer> imageIds = List.of(9999, 8888);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> productImageService.reorderImages(product.getProductId(), imageIds)
        );
        assertTrue(exception.getMessage().contains("Product image not found"));
    }

    @Test
    void reorderImages_WithImageFromDifferentProduct_ThrowsException() {
        // Arrange
        Product anotherProduct = new Product();
        anotherProduct.setName("Another Product");
        anotherProduct.setArticle("TEST-003");
        anotherProduct.setPrice(3000);
        anotherProduct = productRepository.save(anotherProduct);

        ProductImage ourImage = createImage("https://example.com/our.jpg", false, 0);
        productImageRepository.save(ourImage);

        ProductImage theirImage = new ProductImage();
        theirImage.setProduct(anotherProduct);
        theirImage.setImageUrl("https://example.com/their.jpg");
        theirImage.setIsPrimary(false);
        theirImage.setDisplayOrder(0);
        theirImage = productImageRepository.save(theirImage);

        List<Integer> imageIds = List.of(ourImage.getImageId(), theirImage.getImageId());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> productImageService.reorderImages(product.getProductId(), imageIds)
        );
        assertTrue(exception.getMessage().contains("does not belong to this product"));
    }

    private ProductImage createImage(String url, Boolean isPrimary, Integer displayOrder) {
        ProductImage image = new ProductImage();
        image.setProduct(product);
        image.setImageUrl(url);
        image.setIsPrimary(isPrimary);
        image.setDisplayOrder(displayOrder);
        return image;
    }
}
