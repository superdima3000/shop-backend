package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.StoreCreateDto;
import org.example.nirsshop.model.dto.ProductStoreDto;
import org.example.nirsshop.model.dto.StoreDto;
import org.example.nirsshop.repository.ProductStoreRepository;
import org.example.nirsshop.service.ProductStoreService;
import org.example.nirsshop.service.StoreService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stores")
@RequiredArgsConstructor
public class StoreController {

    private final StoreService storeService;
    private final ProductStoreRepository productStoreRepository;

    @GetMapping
    public ResponseEntity<List<StoreDto>> getAllStores() {
        List<StoreDto> stores = storeService.findAll();
        return ResponseEntity.ok(stores);
    }

    @GetMapping("/{id}")
    public ResponseEntity<StoreDto> getStoreById(@PathVariable Integer id) {
        StoreDto store = storeService.findById(id);
        return ResponseEntity.ok(store);
    }

    @PostMapping
    public ResponseEntity<StoreDto> createStore(@RequestBody StoreCreateDto createDto) {
        StoreDto created = storeService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<StoreDto> updateStore(
            @PathVariable Integer id,
            @RequestBody StoreCreateDto createDto) {
        StoreDto updated = storeService.update(id, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteStore(@PathVariable Integer id) {
        storeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Получить все товары в магазине
    @GetMapping("/{id}/products")
    public ResponseEntity<List<ProductStoreDto>> getProductsInStore(@PathVariable Integer id) {
        List<ProductStoreDto> products = productStoreRepository.findByStoreId(id)
                .stream()
                .map(ps -> new ProductStoreDto(
                        ps.getProduct().getProductId(),
                        ps.getStore().getStoreId()
                ))
                .toList();
        return ResponseEntity.ok(products);
    }
}
