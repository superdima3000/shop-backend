package org.example.nirsshop.mapper;

import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.createdto.CategoryCreateDto;
import org.example.nirsshop.model.dto.CategoryDto;
import org.springframework.stereotype.Component;

@Component
public class CategoryMapper implements Mapper<Category, CategoryDto, CategoryCreateDto> {

    @Override
    public CategoryDto toDto(Category entity) {
        if (entity == null) return null;
        Integer parentId = entity.getParent() != null ? entity.getParent().getCategoryId() : null;
        return new CategoryDto(
                entity.getCategoryId(),
                entity.getName(),
                parentId
        );
    }

    @Override
    public Category fromCreateDto(CategoryCreateDto createDto) {
        if (createDto == null) return null;
        // parent проставляется в сервисе по id (загрузить и setParent)
        return Category.builder()
                .name(createDto.name())
                .build();
    }
}

