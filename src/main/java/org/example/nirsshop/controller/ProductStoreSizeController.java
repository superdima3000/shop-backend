package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.ProductStoreSizeCreateDto;
import org.example.nirsshop.model.dto.ProductStoreSizeDto;
import org.example.nirsshop.service.ProductStoreSizeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/product-store-sizes")
@RequiredArgsConstructor
public class ProductStoreSizeController {

    private final ProductStoreSizeService productStoreSizeService;

    @GetMapping
    public ResponseEntity<List<ProductStoreSizeDto>> getAll() {
        return ResponseEntity.ok(productStoreSizeService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductStoreSizeDto> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(productStoreSizeService.findById(id));
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ProductStoreSizeDto>> getByProduct(@PathVariable Integer productId) {
        return ResponseEntity.ok(productStoreSizeService.findByProduct(productId));
    }

    @GetMapping("/product/{productId}/store/{storeId}")
    public ResponseEntity<List<ProductStoreSizeDto>> getByProductAndStore(
            @PathVariable Integer productId,
            @PathVariable Integer storeId) {
        return ResponseEntity.ok(productStoreSizeService.findByProductAndStore(productId, storeId));
    }

    @GetMapping("/product/{productId}/total")
    public ResponseEntity<Integer> getTotalQuantity(@PathVariable Integer productId) {
        return ResponseEntity.ok(productStoreSizeService.getTotalQuantityAllStores(productId));
    }

    @GetMapping("/product/{productId}/store/{storeId}/total")
    public ResponseEntity<Integer> getTotalQuantityInStore(
            @PathVariable Integer productId,
            @PathVariable Integer storeId) {
        return ResponseEntity.ok(productStoreSizeService.getTotalQuantityInStore(productId, storeId));
    }

    @PostMapping
    public ResponseEntity<ProductStoreSizeDto> create(@RequestBody ProductStoreSizeCreateDto createDto) {
        ProductStoreSizeDto created = productStoreSizeService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductStoreSizeDto> update(
            @PathVariable Integer id,
            @RequestBody ProductStoreSizeCreateDto createDto) {
        return ResponseEntity.ok(productStoreSizeService.update(id, createDto));
    }

    @PatchMapping("/{id}/quantity")
    public ResponseEntity<ProductStoreSizeDto> updateQuantity(
            @PathVariable Integer id,
            @RequestParam Integer quantity) {
        return ResponseEntity.ok(productStoreSizeService.updateQuantity(id, quantity));
    }

    @PatchMapping("/{id}/adjust")
    public ResponseEntity<ProductStoreSizeDto> adjustQuantity(
            @PathVariable Integer id,
            @RequestParam Integer delta) {
        return ResponseEntity.ok(productStoreSizeService.adjustQuantity(id, delta));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        productStoreSizeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/check-availability")
    public ResponseEntity<Boolean> checkAvailability(
            @RequestParam Integer productId,
            @RequestParam Integer storeId,
            @RequestParam String size) {
        return ResponseEntity.ok(productStoreSizeService.isSizeAvailable(productId, storeId, size));
    }

    @GetMapping("/sizes/all")
    public ResponseEntity<List<String>> getAllAvailableSizes() {
        List<String> sizes = productStoreSizeService.getAllAvailableSizes();
        return ResponseEntity.ok(sizes);
    }

    @GetMapping("/product/{productId}/size/{size}")
    public ResponseEntity<Integer> getQuantityBySize(
            @PathVariable Integer productId,
            @PathVariable("size") String sizeValue
    ) {
        return ResponseEntity.ok(productStoreSizeService.getQuantityBySize(productId, sizeValue));
    }
}
