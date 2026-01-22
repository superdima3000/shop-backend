package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.ProductImageCreateDto;
import org.example.nirsshop.model.dto.ProductImageDto;
import org.example.nirsshop.service.ProductImageService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products/{productId}/images")
@RequiredArgsConstructor
public class ProductImageController {

    private final ProductImageService productImageService;

    @GetMapping
    public ResponseEntity<List<ProductImageDto>> getProductImages(@PathVariable Integer productId) {
        List<ProductImageDto> images = productImageService.findByProductId(productId);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{imageId}")
    public ResponseEntity<ProductImageDto> getImageById(@PathVariable Integer imageId) {
        ProductImageDto image = productImageService.findById(imageId);
        return ResponseEntity.ok(image);
    }

    @PostMapping
    public ResponseEntity<ProductImageDto> addImage(
            @PathVariable Integer productId,
            @RequestBody ProductImageCreateDto createDto) {
        // Проверяем соответствие productId
        if (!productId.equals(createDto.productId())) {
            throw new IllegalArgumentException("Product ID in path and body must match");
        }
        ProductImageDto created = productImageService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{imageId}")
    public ResponseEntity<ProductImageDto> updateImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId,
            @RequestBody ProductImageCreateDto createDto) {
        ProductImageDto updated = productImageService.update(imageId, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> deleteImage(@PathVariable Integer imageId) {
        productImageService.delete(imageId);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{imageId}/set-primary")
    public ResponseEntity<ProductImageDto> setPrimaryImage(
            @PathVariable Integer productId,
            @PathVariable Integer imageId) {
        ProductImageDto updated = productImageService.setPrimaryImage(productId, imageId);
        return ResponseEntity.ok(updated);
    }

    @PutMapping("/reorder")
    public ResponseEntity<Void> reorderImages(
            @PathVariable Integer productId,
            @RequestBody List<Integer> imageIds) {
        productImageService.reorderImages(productId, imageIds);
        return ResponseEntity.ok().build();
    }
}
