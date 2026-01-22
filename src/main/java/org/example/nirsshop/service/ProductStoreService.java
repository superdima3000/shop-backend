package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.ProductStoreCreateDto;
import org.example.nirsshop.model.dto.ProductStoreDto;

import java.util.List;

public interface ProductStoreService {
    List<ProductStoreDto> findAll();
    ProductStoreDto findById(Integer productId, Integer storeId);
    ProductStoreDto create(ProductStoreCreateDto createDto);
    ProductStoreDto update(Integer productId, Integer storeId, ProductStoreCreateDto createDto);
    void delete(Integer productId, Integer storeId);

}
