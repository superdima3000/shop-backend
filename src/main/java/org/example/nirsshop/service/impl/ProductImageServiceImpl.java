package org.example.nirsshop.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.exception.NotFoundException;
import org.example.nirsshop.mapper.ProductImageMapper;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductImage;
import org.example.nirsshop.model.createdto.ProductImageCreateDto;
import org.example.nirsshop.model.dto.ProductImageDto;
import org.example.nirsshop.repository.ProductImageRepository;
import org.example.nirsshop.repository.ProductRepository;
import org.example.nirsshop.service.ProductImageService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public List<ProductImageDto> findAll() {
        return productImageRepository.findAll()
                .stream()
                .map(productImageMapper::toDto)
                .toList();
    }

    @Override
    public ProductImageDto findById(Integer id) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found: " + id));
        return productImageMapper.toDto(image);
    }

    @Override
    public ProductImageDto create(ProductImageCreateDto createDto) {
        Product product = productRepository.findById(createDto.productId())
                .orElseThrow(() -> new NotFoundException("Product not found: " + createDto.productId()));

        List<ProductImage> existingImages = productImageRepository
                .findByProductProductIdOrderByDisplayOrderAsc(createDto.productId());

        ProductImage image = productImageMapper.fromCreateDto(createDto);
        image.setProduct(product);

        if (existingImages.isEmpty() || Boolean.TRUE.equals(createDto.isPrimary())) {
            existingImages.forEach(img -> img.setIsPrimary(false));

            if (!existingImages.isEmpty()) {
                productImageRepository.saveAll(existingImages);
            }

            image.setIsPrimary(true);
        }

        ProductImage saved = productImageRepository.save(image);
        return productImageMapper.toDto(saved);
    }
    
    @Override
    public ProductImageDto update(Integer id, ProductImageCreateDto createDto) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found: " + id));

        image.setImageUrl(createDto.imageUrl());
        image.setDisplayOrder(createDto.displayOrder() != null ? createDto.displayOrder() : 0);

        if (Boolean.TRUE.equals(createDto.isPrimary())) {
            productImageRepository.findByProductProductIdOrderByDisplayOrderAsc(image.getProduct().getProductId())
                    .forEach(img -> {
                        if (!img.getImageId().equals(id)) {
                            img.setIsPrimary(false);
                        }
                    });
            image.setIsPrimary(true);
        }

        ProductImage saved = productImageRepository.save(image);
        return productImageMapper.toDto(saved);
    }

    @Override
    public void delete(Integer id) {
        ProductImage image = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product image not found: " + id));

        Integer productId = image.getProduct().getProductId();
        boolean wasPrimary = Boolean.TRUE.equals(image.getIsPrimary());

        productImageRepository.deleteById(id);

        if (wasPrimary) {
            List<ProductImage> remaining = productImageRepository
                    .findByProductProductIdOrderByDisplayOrderAsc(productId);
            if (!remaining.isEmpty()) {
                ProductImage newPrimary = remaining.get(0);
                newPrimary.setIsPrimary(true);
                productImageRepository.save(newPrimary);
            }
        }
    }

    @Override
    public List<ProductImageDto> findByProductId(Integer productId) {
        return productImageRepository.findByProductProductIdOrderByDisplayOrderAsc(productId)
                .stream()
                .map(productImageMapper::toDto)
                .toList();
    }

    @Override
    public ProductImageDto setPrimaryImage(Integer productId, Integer imageId) {
        productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundException("Product not found: " + productId));

        ProductImage image = productImageRepository.findById(imageId)
                .orElseThrow(() -> new NotFoundException("Product image not found: " + imageId));

        if (!image.getProduct().getProductId().equals(productId)) {
            throw new IllegalArgumentException("Image does not belong to this product");
        }

        productImageRepository.findByProductProductIdOrderByDisplayOrderAsc(productId)
                .forEach(img -> img.setIsPrimary(false));

        image.setIsPrimary(true);
        ProductImage saved = productImageRepository.save(image);

        return productImageMapper.toDto(saved);
    }

    @Override
    public void reorderImages(Integer productId, List<Integer> imageIds) {
        for (int i = 0; i < imageIds.size(); i++) {
            Integer imageId = imageIds.get(i);
            ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new NotFoundException("Product image not found: " + imageId));

            if (!image.getProduct().getProductId().equals(productId)) {
                throw new IllegalArgumentException("Image does not belong to this product");
            }

            image.setDisplayOrder(i);
            productImageRepository.save(image);
        }
    }
}
