package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ProductStoreMapper;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductStore;
import org.example.nirsshop.model.ProductStoreId;
import org.example.nirsshop.model.Store;
import org.example.nirsshop.model.createdto.ProductStoreCreateDto;
import org.example.nirsshop.model.dto.ProductStoreDto;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.repository.ProductStoreRepository;
import org.example.nirsshop.repository.StoreRepository;
import org.example.nirsshop.service.ProductStoreService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductStoreServiceImpl implements ProductStoreService {

    private final ProductStoreRepository productStoreRepository;
    private final ProductRepository productRepository;
    private final StoreRepository storeRepository;
    private final ProductStoreMapper productStoreMapper;

    @Override
    public List<ProductStoreDto> findAll() {
        return productStoreRepository.findAll()
                .stream()
                .map(productStoreMapper::toDto)
                .toList();
    }

    @Override
    public ProductStoreDto findById(Integer productId, Integer storeId) {
        ProductStore productStore = productStoreRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new NotFoundException("Product store not found: productId=" + productId + ", storeId=" + storeId));
        return productStoreMapper.toDto(productStore);
    }

    @Override
    public ProductStoreDto create(ProductStoreCreateDto createDto) {
        Product product = productRepository.findById(createDto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));

        Store store = storeRepository.findById(createDto.storeId())
                .orElseThrow(() -> new NotFoundException("Store not found: " + createDto.storeId()));

        ProductStore productStore = productStoreMapper.fromCreateDto(createDto);
        productStore.setProduct(product);
        productStore.setStore(store);

        ProductStore saved = productStoreRepository.save(productStore);
        return productStoreMapper.toDto(saved);
    }

    @Override
    public ProductStoreDto update(Integer productId, Integer storeId, ProductStoreCreateDto createDto) {
        ProductStore productStore = productStoreRepository.findByProductIdAndStoreId(productId, storeId)
                .orElseThrow(() -> new NotFoundException("Product store not found: productId=" + productId + ", storeId=" + storeId));

        ProductStore saved = productStoreRepository.save(productStore);
        return productStoreMapper.toDto(saved);
    }

    @Override
    public void delete(Integer productId, Integer storeId) {
        if (!productStoreRepository.existsByProductIdAndStoreId(productId, storeId)) {
            throw new NotFoundException("Product store not found: productId=" + productId + ", storeId=" + storeId);
        }
        productStoreRepository.deleteByProductIdAndStoreId(productId, storeId);
    }
}

