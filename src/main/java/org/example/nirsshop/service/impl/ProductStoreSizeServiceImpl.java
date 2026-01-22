package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ProductStoreSizeMapper;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductStoreSize;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.ProductStoreSizeCreateDto;
import org.example.nirsshop.model.dto.ProductStoreSizeDto;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.repository.ProductStoreSizeRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.ProductStoreSizeService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductStoreSizeServiceImpl implements ProductStoreSizeService {

    private final ProductStoreSizeRepository productStoreSizeRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ProductStoreSizeMapper mapper;

    @Override
    public ProductStoreSizeDto findById(Integer id) {
        ProductStoreSize entity = productStoreSizeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product store size not found: " + id));
        return mapper.toDto(entity);
    }

    @Override
    public List<ProductStoreSizeDto> findAll() {
        return productStoreSizeRepository.findAll().stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    @Transactional
    public ProductStoreSizeDto create(ProductStoreSizeCreateDto createDto) {
        Product product = productRepository.findById(createDto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));
        Store store = storeRepository.findById(createDto.storeId())
                .orElseThrow(() -> new NotFoundException("Store not found: " + createDto.storeId()));

        ProductStoreSize entity = mapper.fromCreateDto(createDto);
        entity.setProduct(product);
        entity.setStore(store);

        ProductStoreSize saved = productStoreSizeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductStoreSizeDto update(Integer id, ProductStoreSizeCreateDto createDto) {
        ProductStoreSize entity = productStoreSizeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product store size not found: " + id));

        entity.setSizeValue(createDto.sizeValue());
        entity.setQuantity(createDto.quantity());

        if (!entity.getProduct().getProductId().equals(createDto.productId())) {
            Product product = productRepository.findById(createDto.productId())
                    .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));
            entity.setProduct(product);
        }

        if (!entity.getStore().getStoreId().equals(createDto.storeId())) {
            Store store = storeRepository.findById(createDto.storeId())
                    .orElseThrow(() -> new NotFoundException("Store not found: " + createDto.storeId()));
            entity.setStore(store);
        }

        ProductStoreSize saved = productStoreSizeRepository.save(entity);
        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public void delete(Integer id) {
        if (!productStoreSizeRepository.existsById(id)) {
            throw new NotFoundException("Product store size not found: " + id);
        }
        productStoreSizeRepository.deleteById(id);
    }

    @Override
    public List<ProductStoreSizeDto> findByProductAndStore(Integer productId, Integer storeId) {
        return productStoreSizeRepository.findByProductProductIdAndStoreStoreId(productId, storeId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public List<ProductStoreSizeDto> findByProduct(Integer productId) {
        return productStoreSizeRepository.findByProductProductId(productId)
                .stream()
                .map(mapper::toDto)
                .toList();
    }

    @Override
    public Integer getTotalQuantityInStore(Integer productId, Integer storeId) {
        return productStoreSizeRepository.getTotalQuantityInStore(productId, storeId);
    }

    @Override
    public Integer getTotalQuantityAllStores(Integer productId) {
        return productStoreSizeRepository.getTotalQuantityAllStores(productId);
    }

    @Override
    @Transactional
    public ProductStoreSizeDto updateQuantity(Integer id, Integer quantity) {
        ProductStoreSize entity = productStoreSizeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product store size not found: " + id));

        entity.setQuantity(quantity);
        ProductStoreSize saved = productStoreSizeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    @Transactional
    public ProductStoreSizeDto adjustQuantity(Integer id, Integer delta) {
        ProductStoreSize entity = productStoreSizeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product store size not found: " + id));

        int newQuantity = entity.getQuantity() + delta;
        if (newQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }

        entity.setQuantity(newQuantity);
        ProductStoreSize saved = productStoreSizeRepository.save(entity);

        return mapper.toDto(saved);
    }

    @Override
    public boolean isSizeAvailable(Integer productId, Integer storeId, String size) {
        return productStoreSizeRepository.isSizeAvailable(productId, storeId, size);
    }

    @Override
    public List<String> getAllAvailableSizes() {
        return productStoreSizeRepository.findAllDistinctSizeValues();
    }

    @Override
    public Integer getQuantityBySize(Integer productId, String sizeValue) {
        return productStoreSizeRepository.getQuantityByProductAndSize(productId, sizeValue);
    }
}
