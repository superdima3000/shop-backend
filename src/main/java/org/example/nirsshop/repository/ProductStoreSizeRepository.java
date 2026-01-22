package org.example.nirsshop.repository;

import org.example.nirsshop.model.ProductStoreSize;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductStoreSizeRepository extends JpaRepository<ProductStoreSize, Integer> {

    List<ProductStoreSize> findByProductProductIdAndStoreStoreId(Integer productId, Integer storeId);
    List<ProductStoreSize> findByProductProductId(Integer productId);

    @Query("SELECT COALESCE(SUM(pss.quantity), 0) FROM ProductStoreSize pss " +
           "WHERE pss.product.productId = :productId AND pss.store.storeId = :storeId")
    Integer getTotalQuantityInStore(@Param("productId") Integer productId,
                                    @Param("storeId") Integer storeId);

    @Query("SELECT COALESCE(SUM(pss.quantity), 0) FROM ProductStoreSize pss " +
           "WHERE pss.product.productId = :productId")
    Integer getTotalQuantityAllStores(@Param("productId") Integer productId);

    @Query("SELECT CASE WHEN COUNT(pss) > 0 THEN true ELSE false END " +
           "FROM ProductStoreSize pss " +
           "WHERE pss.product.productId = :productId " +
           "AND pss.store.storeId = :storeId " +
           "AND pss.sizeValue = :size " +
           "AND pss.quantity > 0")
    boolean isSizeAvailable(@Param("productId") Integer productId,
                            @Param("storeId") Integer storeId,
                            @Param("size") String size);

    @Query("SELECT DISTINCT pss.sizeValue FROM ProductStoreSize pss ORDER BY pss.sizeValue")
    List<String> findAllDistinctSizeValues();

    @Query("SELECT COALESCE(SUM(pss.quantity), 0) FROM ProductStoreSize pss " +
           "WHERE pss.product.productId = :productId AND pss.sizeValue = :sizeValue")
    Integer getQuantityByProductAndSize(@Param("productId") Integer productId,
                                        @Param("sizeValue") String sizeValue);
}
