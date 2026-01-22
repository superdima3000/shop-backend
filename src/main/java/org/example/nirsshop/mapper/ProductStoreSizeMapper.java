package org.example.nirsshop.mapper;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.ProductStoreSize;
import org.example.nirsshop.model.createdto.ProductStoreSizeCreateDto;
import org.example.nirsshop.model.dto.ProductStoreSizeDto;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductStoreSizeMapper implements Mapper<ProductStoreSize, ProductStoreSizeDto, ProductStoreSizeCreateDto> {

    @Override
    public ProductStoreSizeDto toDto(ProductStoreSize entity) {
        if (entity == null) return null;

        String productName = entity.getProduct() != null ? entity.getProduct().getName() : null;
        String storeName = entity.getStore() != null ? entity.getStore().getAddress() : null;

        return new ProductStoreSizeDto(
                entity.getId(),
                entity.getProduct() != null ? entity.getProduct().getProductId() : null,
                entity.getStore() != null ? entity.getStore().getStoreId() : null,
                entity.getSizeValue(),
                entity.getQuantity(),
                productName,
                storeName
        );
    }

    @Override
    public ProductStoreSize fromCreateDto(ProductStoreSizeCreateDto createDto) {
        if (createDto == null) return null;

        return ProductStoreSize.builder()
                .sizeValue(createDto.sizeValue())
                .quantity(createDto.quantity())
                .build();
    }
}
