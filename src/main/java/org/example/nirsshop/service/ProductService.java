package org.example.nirsshop.service;

import org.example.nirsshop.model.createdto.ProductCreateDto;
import org.example.nirsshop.model.dto.*;
import org.example.nirsshop.repository.PopularProductProjection;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductService extends CrudService<ProductDto, ProductCreateDto, Integer> {
    Page<ProductDto> findByFilters(
            Integer categoryId,
            Integer minPrice,
            Integer maxPrice,
            String gender,
            List<String> sizeValues,
            String search,
            Boolean inStock,
            Integer storeId,
            Double rating,
            Pageable pageable);

    List<StoreDto> getStoresWithProduct(Integer productId);

    List<PopularProductDto> getTopSellingProducts(int limit);
    List<String> getAvailableSizes(Integer productId);
    Integer getTotalQuantity(Integer productId);
    Integer getQuantityInStore(Integer productId, Integer storeId);
    List<ProductDto> searchByName(String query);
    boolean isProductInStock(Integer productId, Integer storeId);
    List<TopIncomeProductDto> getTopIncomeProducts(int limit);

    List<TopCleanIncomeProductDto> getTopCleanIncomeProducts(int limit);

    List<ProductStatsDto> getProductStats(int limit, String orderBy);
}


