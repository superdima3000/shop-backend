package org.example.nirsshop.repository;

import org.example.nirsshop.model.ProductStore;
import org.example.nirsshop.model.ProductStoreId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductStoreRepository extends JpaRepository<ProductStore, ProductStoreId> {

    @Query("SELECT ps FROM ProductStore ps WHERE ps.store.storeId = :storeId")
    List<ProductStore> findByStoreId(@Param("storeId") Integer storeId);

    @Query("SELECT ps FROM ProductStore ps WHERE ps.product.productId = :productId")
    List<ProductStore> findByProductId(@Param("productId") Integer productId);

    @Query("SELECT ps FROM ProductStore ps WHERE ps.product.productId = :productId AND ps.store.storeId = :storeId")
    Optional<ProductStore> findByProductIdAndStoreId(@Param("productId") Integer productId, @Param("storeId") Integer storeId);

    @Query("SELECT CASE WHEN COUNT(ps) > 0 THEN true ELSE false END FROM ProductStore ps WHERE ps.product.productId = :productId AND ps.store.storeId = :storeId")
    boolean existsByProductIdAndStoreId(@Param("productId") Integer productId, @Param("storeId") Integer storeId);

    @Modifying
    @Query("DELETE FROM ProductStore ps WHERE ps.product.productId = :productId AND ps.store.storeId = :storeId")
    void deleteByProductIdAndStoreId(@Param("productId") Integer productId, @Param("storeId") Integer storeId);
}
