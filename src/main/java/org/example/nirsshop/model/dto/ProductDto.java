package org.example.nirsshop.model.dto;

import org.example.nirsshop.model.dto.ProductImageDto;

import java.util.List;

public record ProductDto(
        Integer id,
        String name,
        String article,
        Integer price,
        Integer weight,
        String description,
        String gender,
        Double rating,
        Integer categoryId,
        String primaryImageUrl,  // главная картинка
        List<ProductImageDto> images,
        List<String> availableSizes, // ДОБАВИЛИ - список доступных размеров
        Integer totalQuantity// все картинки
) {}
