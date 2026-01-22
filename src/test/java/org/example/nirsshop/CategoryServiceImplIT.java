package org.example.nirsshop;

import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.createdto.CategoryCreateDto;
import org.example.nirsshop.model.dto.CategoryDto;
import org.example.nirsshop.repository.CategoryRepository;
import org.example.nirsshop.service.CategoryService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
public class CategoryServiceImplIT {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CategoryRepository categoryRepository;

    @Test
    void findAll_ReturnsAllCategories() {
        // Arrange
        Category category1 = createCategory("Clothing");
        Category category2 = createCategory("Shoes");
        categoryRepository.saveAll(List.of(category1, category2));

        // Act
        List<CategoryDto> result = categoryService.findAll();

        // Assert
        assertTrue(result.size() >= 2);
    }

    @Test
    void findById_ExistingCategory_ReturnsCategoryDto() {
        // Arrange
        Category category = createCategory("Accessories");
        Category saved = categoryRepository.save(category);

        // Act
        CategoryDto result = categoryService.findById(saved.getCategoryId());

        // Assert
        assertNotNull(result);
        assertEquals("Accessories", result.name());
        assertNull(result.parentId());
    }

    @Test
    void findById_NonExistingCategory_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> categoryService.findById(9999)
        );
        assertTrue(exception.getMessage().contains("Category not found"));
    }

    @Test
    void create_WithoutParent_CreatesCategory() {
        // Arrange
        CategoryCreateDto createDto = new CategoryCreateDto("Sportswear", null);

        // Act
        CategoryDto result = categoryService.create(createDto);

        // Assert
        assertNotNull(result);
        assertNotNull(result.id());
        assertEquals("Sportswear", result.name());
        assertNull(result.parentId());

        Category savedCategory = categoryRepository.findById(result.id()).orElseThrow();
        assertEquals("Sportswear", savedCategory.getName());
        assertNull(savedCategory.getParent());
    }

    @Test
    void create_WithParent_CreatesCategory() {
        // Arrange
        Category parent = createCategory("Outerwear");
        Category savedParent = categoryRepository.save(parent);

        CategoryCreateDto createDto = new CategoryCreateDto("Jackets", savedParent.getCategoryId());

        // Act
        CategoryDto result = categoryService.create(createDto);

        // Assert
        assertNotNull(result);
        assertEquals("Jackets", result.name());
        assertEquals(savedParent.getCategoryId(), result.parentId());

        Category savedCategory = categoryRepository.findById(result.id()).orElseThrow();
        assertEquals("Jackets", savedCategory.getName());
        assertEquals(savedParent.getCategoryId(), savedCategory.getParent().getCategoryId());
    }

    @Test
    void create_WithNonExistingParent_ThrowsNotFoundException() {
        // Arrange
        CategoryCreateDto createDto = new CategoryCreateDto("Subcategory", 9999);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> categoryService.create(createDto)
        );
        assertTrue(exception.getMessage().contains("Parent category not found"));
    }

    @Test
    void update_ExistingCategory_UpdatesSuccessfully() {
        // Arrange
        Category category = createCategory("Old Name");
        Category saved = categoryRepository.save(category);

        CategoryCreateDto updateDto = new CategoryCreateDto("New Name", null);

        // Act
        CategoryDto result = categoryService.update(saved.getCategoryId(), updateDto);

        // Assert
        assertEquals("New Name", result.name());
        assertNull(result.parentId());

        Category updatedCategory = categoryRepository.findById(saved.getCategoryId()).orElseThrow();
        assertEquals("New Name", updatedCategory.getName());
        assertNull(updatedCategory.getParent());
    }

    @Test
    void update_SetParent_UpdatesSuccessfully() {
        // Arrange
        Category category = createCategory("Category");
        Category saved = categoryRepository.save(category);

        Category parent = createCategory("Parent");
        Category savedParent = categoryRepository.save(parent);

        CategoryCreateDto updateDto = new CategoryCreateDto("Category", savedParent.getCategoryId());

        // Act
        CategoryDto result = categoryService.update(saved.getCategoryId(), updateDto);

        // Assert
        assertEquals("Category", result.name());
        assertEquals(savedParent.getCategoryId(), result.parentId());

        Category updatedCategory = categoryRepository.findById(saved.getCategoryId()).orElseThrow();
        assertEquals(savedParent.getCategoryId(), updatedCategory.getParent().getCategoryId());
    }

    @Test
    void update_RemoveParent_UpdatesSuccessfully() {
        // Arrange
        Category parent = createCategory("Parent");
        Category savedParent = categoryRepository.save(parent);

        Category category = createCategory("Child");
        category.setParent(savedParent);
        Category saved = categoryRepository.save(category);

        CategoryCreateDto updateDto = new CategoryCreateDto("Child", null);

        // Act
        CategoryDto result = categoryService.update(saved.getCategoryId(), updateDto);

        // Assert
        assertEquals("Child", result.name());
        assertNull(result.parentId());

        Category updatedCategory = categoryRepository.findById(saved.getCategoryId()).orElseThrow();
        assertNull(updatedCategory.getParent());
    }

    @Test
    void update_NonExistingCategory_ThrowsNotFoundException() {
        // Arrange
        CategoryCreateDto updateDto = new CategoryCreateDto("Name", null);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> categoryService.update(9999, updateDto)
        );
        assertTrue(exception.getMessage().contains("Category not found"));
    }

    @Test
    void update_WithNonExistingParent_ThrowsNotFoundException() {
        // Arrange
        Category category = createCategory("Category");
        Category saved = categoryRepository.save(category);

        CategoryCreateDto updateDto = new CategoryCreateDto("Category", 9999);

        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> categoryService.update(saved.getCategoryId(), updateDto)
        );
        assertTrue(exception.getMessage().contains("Parent category not found"));
    }

    @Test
    void delete_ExistingCategory_DeletesSuccessfully() {
        // Arrange
        Category category = createCategory("To Delete");
        Category saved = categoryRepository.save(category);
        Integer categoryId = saved.getCategoryId();

        // Act
        categoryService.delete(categoryId);

        // Assert
        assertFalse(categoryRepository.existsById(categoryId));
    }

    @Test
    void delete_NonExistingCategory_ThrowsNotFoundException() {
        // Act & Assert
        NotFoundException exception = assertThrows(
                NotFoundException.class,
                () -> categoryService.delete(9999)
        );
        assertTrue(exception.getMessage().contains("Category not found"));
    }

    private Category createCategory(String name) {
        Category category = new Category();
        category.setName(name);
        return category;
    }
}

