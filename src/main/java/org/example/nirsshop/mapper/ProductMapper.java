package org.example.nirsshop.mapper;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductStoreSize;
import org.example.nirsshop.model.createdto.ProductCreateDto;
import org.example.nirsshop.model.dto.ProductDto;
import org.example.nirsshop.model.dto.ProductImageDto;
import org.example.nirsshop.repository.ProductStoreSizeRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;


@Component
@RequiredArgsConstructor
public class ProductMapper implements Mapper<Product, ProductDto, ProductCreateDto> {

    private final ProductImageMapper productImageMapper;
    private final ProductStoreSizeRepository productStoreSizeRepository;

    @Override
    public ProductDto toDto(Product entity) {
        if (entity == null) return null;

        Integer categoryId = entity.getCategory() != null ? entity.getCategory().getCategoryId() : null;
        String primaryImageUrl = entity.getPrimaryImageUrl();

        List<ProductImageDto> images = entity.getImages() != null
                ? entity.getImages().stream()
                .map(productImageMapper::toDto)
                .toList()
                : List.of();
        List<String> availableSizes = productStoreSizeRepository
                .findByProductProductId(entity.getProductId())
                .stream()
                .map(ProductStoreSize::getSizeValue)
                .distinct()
                .sorted()
                .toList();

        Integer totalQuantity = productStoreSizeRepository
                .getTotalQuantityAllStores(entity.getProductId());

        return new ProductDto(
                entity.getProductId(),
                entity.getName(),
                entity.getArticle(),
                entity.getPrice(),
                entity.getWeight(),
                entity.getDescription(),
                entity.getGender(),
                entity.getRating(),
                categoryId,
                primaryImageUrl,
                images,
                availableSizes,
                totalQuantity
        );
    }


    @Override
    public Product fromCreateDto(ProductCreateDto createDto) {
        if (createDto == null) return null;
        return Product.builder()
                .name(createDto.name())
                .article(createDto.article())
                .price(createDto.price())
                .weight(createDto.weight())
                .description(createDto.description())
                .gender(createDto.gender())
                .rating(createDto.rating())
                .build();
    }
}
