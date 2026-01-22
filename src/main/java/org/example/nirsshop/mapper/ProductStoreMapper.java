package org.example.nirsshop.mapper;

import org.example.nirsshop.model.ProductStore;
import org.example.nirsshop.model.ProductStoreId;
import org.example.nirsshop.model.createdto.ProductStoreCreateDto;
import org.example.nirsshop.model.dto.ProductStoreDto;
import org.springframework.stereotype.Component;

@Component
public class ProductStoreMapper implements Mapper<ProductStore, ProductStoreDto, ProductStoreCreateDto> {

    @Override
    public ProductStoreDto toDto(ProductStore entity) {
        if (entity == null) return null;
        return new ProductStoreDto(
                entity.getProduct().getProductId(),
                entity.getStore().getStoreId()
        );
    }

    @Override
    public ProductStore fromCreateDto(ProductStoreCreateDto createDto) {
        if (createDto == null) return null;
        ProductStore ps = new ProductStore();
        ProductStoreId id = new ProductStoreId();
        id.setProductId(createDto.productId());
        id.setStoreId(createDto.storeId());
        ps.setId(id);
        // Product и Store загружаются в сервисе и сетятся
        return ps;
    }
}

