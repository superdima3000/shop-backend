package org.example.nirsshop.specification;

import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.example.nirsshop.model.Product;
import org.example.nirsshop.model.ProductGlobalStock;
import org.example.nirsshop.model.ProductStore;
import org.example.nirsshop.model.ProductStoreSize;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public class ProductSpecification {

    public static Specification<Product> hasCategory(Integer categoryId) {
        return (root, query, cb) ->
                categoryId == null ? null : cb.equal(root.get("category").get("categoryId"), categoryId);
    }

    public static Specification<Product> priceBetween(Integer minPrice, Integer maxPrice) {
        return (root, query, cb) -> {
            if (minPrice == null && maxPrice == null) return null;
            if (minPrice == null) return cb.lessThanOrEqualTo(root.get("price"), maxPrice);
            if (maxPrice == null) return cb.greaterThanOrEqualTo(root.get("price"), minPrice);
            return cb.between(root.get("price"), minPrice, maxPrice);
        };
    }

    public static Specification<Product> ratingHigherThan(Double rating) {
        return (root, query, cb) ->
                rating == null ? null : cb.greaterThanOrEqualTo(root.get("rating"), rating);
    }


    public static Specification<Product> hasGender(String gender) {
        return (root, query, cb) ->
                gender == null ? null : cb.equal(root.get("gender"), gender);
    }

    public static Specification<Product> hasSize(Integer size) {
        return (root, query, cb) ->
                size == null ? null : cb.equal(root.get("size"), size);
    }

    public static Specification<Product> nameOrArticleContains(String search) {
        return (root, query, cb) -> {
            if (search == null || search.isBlank()) return null;
            String pattern = "%" + search.toLowerCase() + "%";
            return cb.or(
                    cb.like(cb.lower(root.get("name")), pattern),
                    cb.like(cb.lower(root.get("article")), pattern)
            );
        };
    }

    // Фильтр по наличию на глобальном складе
    public static Specification<Product> availableInGlobalStock() {
        return (root, query, cb) -> {
            Join<Product, ProductGlobalStock> globalStock = root.join("globalStock", JoinType.LEFT);
            return cb.greaterThan(globalStock.get("quantity"), 0);
        };
    }

    // Фильтр по наличию хотя бы в одном магазине
    public static Specification<Product> availableInAnyStore() {
        return (root, query, cb) -> {
            Join<Product, ProductStore> productStore = root.join("productStoreSizes", JoinType.INNER);
            return cb.greaterThan(productStore.get("quantity"), 0);
        };
    }

    // Фильтр по наличию в конкретном магазине
    public static Specification<Product> availableInStore(Integer storeId) {
        return (root, query, cb) -> {
            if (storeId == null) return null;
            Join<Product, ProductStore> productStore = root.join("productStoreSizes", JoinType.INNER);
            return cb.and(
                    cb.equal(productStore.get("store").get("storeId"), storeId),
                    cb.greaterThan(productStore.get("quantity"), 0)
            );
        };
    }

    public static Specification<Product> hasAnySizeValue(List<String> sizeValues) {
        return (root, query, cb) -> {
            if (sizeValues == null || sizeValues.isEmpty()) {
                return null;
            }
            Join<Product, ProductStoreSize> productStoreSizeJoin = root.join("productStoreSizes", JoinType.INNER);

            query.distinct(true);

            return productStoreSizeJoin.get("sizeValue").in(sizeValues);
        };
    }

    public static Specification<Product> isInStock() {
        return (root, query, cb) -> {
            Join<Product, ProductStoreSize> productStoreSizeJoin = root.join("productStoreSizes", JoinType.INNER);
            query.distinct(true);
            return cb.greaterThan(productStoreSizeJoin.get("quantity"), 0);
        };
    }
}

