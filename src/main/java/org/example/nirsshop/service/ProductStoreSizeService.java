package org.example.nirsshop.service;

import org.example.nirsshop.model.dto.ProductStoreSizeDto;
import org.example.nirsshop.model.createdto.ProductStoreSizeCreateDto;

import java.util.List;

public interface ProductStoreSizeService extends CrudService<ProductStoreSizeDto, ProductStoreSizeCreateDto, Integer> {
    List<ProductStoreSizeDto> findByProductAndStore(Integer productId, Integer storeId);
    List<ProductStoreSizeDto> findByProduct(Integer productId);
    Integer getTotalQuantityInStore(Integer productId, Integer storeId);
    Integer getTotalQuantityAllStores(Integer productId);
    ProductStoreSizeDto updateQuantity(Integer id, Integer quantity);
    ProductStoreSizeDto adjustQuantity(Integer id, Integer delta);
    boolean isSizeAvailable(Integer productId, Integer storeId, String size);

    List<String> getAllAvailableSizes();
    Integer getQuantityBySize(Integer productId, String sizeValue);
}
