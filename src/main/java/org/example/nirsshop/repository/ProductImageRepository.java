package org.example.nirsshop.repository;

import org.example.nirsshop.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Integer> {

    List<ProductImage> findByProductProductIdOrderByDisplayOrderAsc(Integer productId);

    Optional<ProductImage> findByProductProductIdAndIsPrimaryTrue(Integer productId);

    @Query("SELECT pi FROM ProductImage pi WHERE pi.product.productId = :productId AND pi.isPrimary = true")
    Optional<ProductImage> findPrimaryImageByProductId(@Param("productId") Integer productId);

    void deleteByProductProductId(Integer productId);
}
