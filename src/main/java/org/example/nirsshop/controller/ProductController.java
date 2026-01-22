package org.example.nirsshop.controller;

import lombok.RequiredArgsConstructor;
import org.example.nirsshop.model.createdto.ProductCreateDto;
import org.example.nirsshop.model.createdto.ProductGlobalStockCreateDto;
import org.example.nirsshop.model.createdto.ProductStoreCreateDto;
import org.example.nirsshop.model.dto.*;
import org.example.nirsshop.service.ProductGlobalStockService;
import org.example.nirsshop.service.ProductService;
import org.example.nirsshop.service.ProductStoreService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;
    private final ProductGlobalStockService productGlobalStockService;
    private final ProductStoreService productStoreService;

    @GetMapping
    public ResponseEntity<Page<ProductDto>> getAllProducts(
            @RequestParam(required = false) Integer categoryId,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) List<String> sizeValues, // НОВЫЙ ПАРАМЕТР
            @RequestParam(required = false) String search,
            @RequestParam(required = false) Boolean inStock,
            @RequestParam(required = false) Integer storeId,
            @RequestParam(required = false) Double rating,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int pageSize) {

        Pageable pageable = PageRequest.of(page, pageSize);

        Page<ProductDto> products = productService.findByFilters(
                categoryId, minPrice, maxPrice, gender, sizeValues,
                search, inStock, storeId, rating, pageable
        );
        return ResponseEntity.ok(products);
    }


    @GetMapping("/{id}")
    public ResponseEntity<ProductDto> getProductById(@PathVariable Integer id) {
        ProductDto product = productService.findById(id);
        return ResponseEntity.ok(product);
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@RequestBody ProductCreateDto createDto) {
        ProductDto created = productService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductDto> updateProduct(
            @PathVariable Integer id,
            @RequestBody ProductCreateDto createDto) {
        ProductDto updated = productService.update(id, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Integer id) {
        productService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Глобальный склад
    @GetMapping("/{id}/global-stock")
    public ResponseEntity<ProductGlobalStockDto> getGlobalStock(@PathVariable Integer id) {
        ProductGlobalStockDto stock = productGlobalStockService.findById(id);
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/{id}/global-stock")
    public ResponseEntity<ProductGlobalStockDto> createGlobalStock(
            @PathVariable Integer id,
            @RequestBody ProductGlobalStockCreateDto createDto) {
        // Проверяем, что id в пути совпадает с id в теле
        if (!id.equals(createDto.productId())) {
            throw new IllegalArgumentException("Product ID in path and body must match");
        }
        ProductGlobalStockDto created = productGlobalStockService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}/global-stock")
    public ResponseEntity<ProductGlobalStockDto> updateGlobalStock(
            @PathVariable Integer id,
            @RequestBody ProductGlobalStockCreateDto createDto) {
        ProductGlobalStockDto updated = productGlobalStockService.update(id, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}/global-stock")
    public ResponseEntity<Void> deleteGlobalStock(@PathVariable Integer id) {
        productGlobalStockService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // Магазины с товаром
    @GetMapping("/{id}/stores")
    public ResponseEntity<List<StoreDto>> getStoresWithProduct(@PathVariable Integer id) {
        List<StoreDto> stores = productService.getStoresWithProduct(id);
        return ResponseEntity.ok(stores);
    }

    // Остаток товара в конкретном магазине
    @GetMapping("/{productId}/stores/{storeId}/stock")
    public ResponseEntity<ProductStoreDto> getProductStoreStock(
            @PathVariable Integer productId,
            @PathVariable Integer storeId) {
        ProductStoreDto stock = productStoreService.findById(productId, storeId);
        return ResponseEntity.ok(stock);
    }

    @PostMapping("/{productId}/stores/{storeId}/stock")
    public ResponseEntity<ProductStoreDto> createProductStoreStock(
            @PathVariable Integer productId,
            @PathVariable Integer storeId,
            @RequestBody ProductStoreCreateDto createDto) {
        // Проверяем соответствие ID
        if (!productId.equals(createDto.productId()) || !storeId.equals(createDto.storeId())) {
            throw new IllegalArgumentException("IDs in path and body must match");
        }
        ProductStoreDto created = productStoreService.create(createDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{productId}/stores/{storeId}/stock")
    public ResponseEntity<ProductStoreDto> updateProductStoreStock(
            @PathVariable Integer productId,
            @PathVariable Integer storeId,
            @RequestBody ProductStoreCreateDto createDto) {
        ProductStoreDto updated = productStoreService.update(productId, storeId, createDto);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{productId}/stores/{storeId}/stock")
    public ResponseEntity<Void> deleteProductStoreStock(
            @PathVariable Integer productId,
            @PathVariable Integer storeId) {
        productStoreService.delete(productId, storeId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/top-selling")
    public ResponseEntity<List<PopularProductDto>> getTopSellingProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<PopularProductDto> products = productService.getTopSellingProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-income")
    public ResponseEntity<List<TopIncomeProductDto>> getTopIncomeProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopIncomeProductDto> products = productService.getTopIncomeProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/top-clean-income")
    public ResponseEntity<List<TopCleanIncomeProductDto>> getTopCleanIncomeProducts(
            @RequestParam(defaultValue = "10") int limit) {
        List<TopCleanIncomeProductDto> products = productService.getTopCleanIncomeProducts(limit);
        return ResponseEntity.ok(products);
    }

    @GetMapping("/stats")
    public ResponseEntity<List<ProductStatsDto>> getProductStats(
            @RequestParam(defaultValue = "10") int limit,
            @RequestParam(defaultValue = "total_sold") String orderBy) {
        List<ProductStatsDto> products = productService.getProductStats(limit, orderBy);
        return ResponseEntity.ok(products);
    }



    /**
     * Получить доступные размеры товара
     * GET /api/products/{id}/sizes
     */
    @GetMapping("/{id}/sizes")
    public ResponseEntity<List<String>> getAvailableSizes(@PathVariable Integer id) {
        List<String> sizes = productService.getAvailableSizes(id);
        return ResponseEntity.ok(sizes);
    }

    /**
     * Получить общее количество товара во всех магазинах
     * GET /api/products/{id}/total-quantity
     */
    @GetMapping("/{id}/total-quantity")
    public ResponseEntity<Integer> getTotalQuantity(@PathVariable Integer id) {
        Integer quantity = productService.getTotalQuantity(id);
        return ResponseEntity.ok(quantity);
    }

    /**
     * Получить количество товара в конкретном магазине
     * GET /api/products/{id}/store/{storeId}/quantity
     */
    @GetMapping("/{id}/store/{storeId}/quantity")
    public ResponseEntity<Integer> getQuantityInStore(
            @PathVariable Integer id,
            @PathVariable Integer storeId) {
        Integer quantity = productService.getQuantityInStore(id, storeId);
        return ResponseEntity.ok(quantity);
    }


    /**
     * Проверить наличие товара в магазине
     * GET /api/products/{id}/store/{storeId}/in-stock
     */
    @GetMapping("/{id}/store/{storeId}/in-stock")
    public ResponseEntity<Boolean> isProductInStock(
            @PathVariable Integer id,
            @PathVariable Integer storeId) {
        boolean inStock = productService.isProductInStock(id, storeId);
        return ResponseEntity.ok(inStock);
    }


    /**
     * Поиск товаров по имени
     * GET /api/products/search?query=куртка
     */
    @GetMapping("/search")
    public ResponseEntity<List<ProductDto>> searchProducts(@RequestParam String query) {
        List<ProductDto> products = productService.searchByName(query);
        return ResponseEntity.ok(products);
    }


}
