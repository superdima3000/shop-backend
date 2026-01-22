package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.ProductImageCreateDto;
import org.example.nirsshop.model.dto.ProductImageDto;

import java.util.List;

public interface ProductImageService extends CrudService<ProductImageDto, ProductImageCreateDto, Integer> {

    // Дополнительные методы специфичные для картинок
    List<ProductImageDto> findByProductId(Integer productId);
    ProductImageDto setPrimaryImage(Integer productId, Integer imageId);
    void reorderImages(Integer productId, List<Integer> imageIds);
}
