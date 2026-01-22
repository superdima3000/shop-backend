package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ProductMapper;
import org.example.nirsshop.mapper.StoreMapper;
import org.example.nirsshop.model.Category;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductStoreSize;
import org.example.nirsshop.model.createdto.ProductCreateDto;
import org.example.nirsshop.model.dto.*;
import org.example.nirsshop.repository.*;
import org.example.nirsshop.service.ProductService;
import org.example.nirsshop.specification.ProductSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ProductMapper productMapper;
    private final ProductStoreRepository productStoreRepository;
    private final StoreMapper storeMapper;
    private final ProductStoreSizeRepository productStoreSizeRepository;

    @Override
    public List<ProductDto> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

    @Override
    public ProductDto findById(Integer id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));
        return productMapper.toDto(product);
    }

    @Override
    public ProductDto create(ProductCreateDto createDto) {
        Product product = productMapper.fromCreateDto(createDto);

        if (createDto.categoryId() != null) {
            Category category = categoryRepository.findById(createDto.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + createDto.categoryId()));
            product.setCategory(category);
        }

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    public ProductDto update(Integer id, ProductCreateDto createDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found: " + id));

        product.setName(createDto.name());
        product.setArticle(createDto.article());
        product.setPrice(createDto.price());
        product.setRating(createDto.rating());
        product.setWeight(createDto.weight());
        product.setDescription(createDto.description());
        product.setGender(createDto.gender());

        if (createDto.categoryId() != null) {
            Category category = categoryRepository.findById(createDto.categoryId())
                    .orElseThrow(() -> new NotFoundException("Category not found: " + createDto.categoryId()));
            product.setCategory(category);
        } else {
            product.setCategory(null);
        }

        Product saved = productRepository.save(product);
        return productMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        if (!productRepository.existsById(id)) {
            throw new NotFoundException("Product not found: " + id);
        }
        productRepository.deleteById(id);
    }

    @Override
    public Page<ProductDto> findByFilters(
            Integer categoryId,
            Integer minPrice,
            Integer maxPrice,
            String gender,
            List<String> sizeValues,
            String search,
            Boolean inStock,
            Integer storeId,
            Double rating,
            Pageable pageable) {

        Specification<Product> spec = (root, query, cb) -> null;

        spec = spec.and(ProductSpecification.hasCategory(categoryId));
        spec = spec.and(ProductSpecification.priceBetween(minPrice, maxPrice));
        spec = spec.and(ProductSpecification.hasGender(gender));
        spec = spec.and(ProductSpecification.hasAnySizeValue(sizeValues));
        spec = spec.and(ProductSpecification.ratingHigherThan(rating));
        spec = spec.and(ProductSpecification.nameOrArticleContains(search));

        if (Boolean.TRUE.equals(inStock)) {
            if (storeId != null) {
                spec = spec.and(ProductSpecification.availableInStore(storeId));
            } else {
                spec = spec.and(ProductSpecification.availableInAnyStore());
            }
        }

        return productRepository.findAll(spec, pageable)
                .map(productMapper::toDto);
    }

    @Override
    public List<StoreDto> getStoresWithProduct(Integer productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        return productStoreSizeRepository.findByProductProductId(productId)
                .stream()
                .filter(pss -> pss.getQuantity() > 0)
                .map(ProductStoreSize::getStore)
                .distinct()
                .map(storeMapper::toDto)
                .toList();
    }


    @Override
    public List<PopularProductDto> getTopSellingProducts(int limit) {
        List<PopularProductProjection> projections = productRepository.findTopSellingProducts(limit);
        return projections.stream()
                .map(p -> new PopularProductDto(
                        p.getProductId(),
                        p.getName(),
                        p.getArticle(),
                        p.getPrice(),
                        p.getRating(),
                        p.getTotalSold()
                ))
                .toList();

    }

    @Override
    public Integer getTotalQuantity(Integer productId) {
        return productStoreSizeRepository.getTotalQuantityAllStores(productId);
    }

    @Override
    public List<String> getAvailableSizes(Integer productId) {
        return productStoreSizeRepository.findByProductProductId(productId)
                .stream()
                .map(ProductStoreSize::getSizeValue)
                .distinct()
                .sorted()
                .toList();
    }

    @Override
    public Integer getQuantityInStore(Integer productId, Integer storeId) {
        Integer quantity = productStoreSizeRepository.getTotalQuantityInStore(productId, storeId);
        return quantity != null ? quantity : 0;
    }

    @Override
    public boolean isProductInStock(Integer productId, Integer storeId) {
        Integer quantity = getQuantityInStore(productId, storeId);
        return quantity > 0;
    }

    @Override
    public List<TopIncomeProductDto> getTopIncomeProducts(int limit) {
        List<TopIncomeProductProjection> projections = productRepository.findTopIncomeProducts(limit);
        return projections.stream()
                .map(p -> new TopIncomeProductDto(
                        p.getProductId(),
                        p.getName(),
                        p.getTotalIncome(),
                        p.getTotalSold()
                ))
                .toList();

    }

    @Override
    public List<TopCleanIncomeProductDto> getTopCleanIncomeProducts(int limit) {
        List<TopCleanIncomeProductProjection> projections = productRepository.findTopCleanIncomeProducts(limit);
        return projections.stream()
                .map(p -> new TopCleanIncomeProductDto(
                        p.getProductId(),
                        p.getName(),
                        p.getCleanIncome(),
                        p.getTotalSold()
                ))
                .toList();
    }

    @Override
    public List<ProductStatsDto> getProductStats(int limit, String orderBy) {
        List<ProductStatsProjection> projections = productRepository.getProductStats(limit, orderBy);
        return projections.stream()
                .map(p -> new ProductStatsDto(
                        p.getProductId(),
                        p.getName(),
                        p.getRating(),
                        p.getCleanIncome(),
                        p.getTotalIncome(),
                        p.getTotalSold()
                ))
                .toList();
    }


    @Override
    public List<ProductDto> searchByName(String query) {
        // Поиск по частичному совпадению (регистронезависимый)
        return productRepository.findByNameContainingIgnoreCase(query)
                .stream()
                .map(productMapper::toDto)
                .toList();
    }

}
