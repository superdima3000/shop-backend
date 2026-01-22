package org.example.nirsshop.mapper;

import org.example.nirsshop.model.ProductImage;
import org.example.nirsshop.model.createdto.ProductImageCreateDto;
import org.example.nirsshop.model.dto.ProductImageDto;
import org.springframework.stereotype.Component;

@Component
public class ProductImageMapper implements Mapper<ProductImage, ProductImageDto, ProductImageCreateDto> {

    @Override
    public ProductImageDto toDto(ProductImage entity) {
        if (entity == null) {
            return null;
        }
        return new ProductImageDto(
                entity.getImageId(),
                entity.getImageUrl(),
                entity.getIsPrimary(),
                entity.getDisplayOrder()
        );
    }

    @Override
    public ProductImage fromCreateDto(ProductImageCreateDto createDto) {
        if (createDto == null) {
            return null;
        }
        ProductImage image = new ProductImage();
        image.setImageUrl(createDto.imageUrl());
        image.setIsPrimary(createDto.isPrimary() != null ? createDto.isPrimary() : false);
        image.setDisplayOrder(createDto.displayOrder() != null ? createDto.displayOrder() : 0);
        return image;
    }
}
