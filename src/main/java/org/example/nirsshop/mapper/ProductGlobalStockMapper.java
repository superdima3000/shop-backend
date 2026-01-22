package org.example.nirsshop.mapper;

import org.example.nirsshop.model.ProductGlobalStock;
import org.example.nirsshop.model.createdto.ProductGlobalStockCreateDto;
import org.example.nirsshop.model.dto.ProductGlobalStockDto;
import org.springframework.stereotype.Component;

@Component
public class ProductGlobalStockMapper implements Mapper<ProductGlobalStock, ProductGlobalStockDto, ProductGlobalStockCreateDto> {

    @Override
    public ProductGlobalStockDto toDto(ProductGlobalStock entity) {
        if (entity == null) return null;
        return new ProductGlobalStockDto(
                entity.getProduct().getProductId(),
                entity.getQuantity(),
                entity.getPrice()
        );
    }

    @Override
    public ProductGlobalStock fromCreateDto(ProductGlobalStockCreateDto createDto) {
        if (createDto == null) return null;
        ProductGlobalStock gs = new ProductGlobalStock();
        gs.setQuantity(createDto.quantity());
        gs.setPrice(createDto.price());
        // Product загружается в сервисе и сетится + MapsId
        return gs;
    }
}

