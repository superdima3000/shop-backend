package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.CategoryMapper;
import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.createdto.CategoryCreateDto;
import org.example.nirsshop.model.dto.CategoryDto;
import org.example.nirsshop.repository.CategoryRepository;
import org.example.nirsshop.service.CategoryService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public List<CategoryDto> findAll() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    @Override
    public CategoryDto findById(Integer id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));
        return categoryMapper.toDto(category);
    }

    @Override
    public CategoryDto create(CategoryCreateDto createDto) {
        Category category = categoryMapper.fromCreateDto(createDto);

        if (createDto.parentId() != null) {
            Category parent = categoryRepository.findById(createDto.parentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found: " + createDto.parentId()));
            category.setParent(parent);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public CategoryDto update(Integer id, CategoryCreateDto createDto) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Category not found: " + id));

        category.setName(createDto.name());

        if (createDto.parentId() != null) {
            Category parent = categoryRepository.findById(createDto.parentId())
                    .orElseThrow(() -> new NotFoundException("Parent category not found: " + createDto.parentId()));
            category.setParent(parent);
        } else {
            category.setParent(null);
        }

        Category saved = categoryRepository.save(category);
        return categoryMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Category not found: " + id);
        }
        categoryRepository.deleteById(id);
    }
}
