package org.example.nirsshop.repository;

import org.example.nirsshop.model.ProductOrder;
import org.example.nirsshop.model.ProductOrderId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductOrderRepository extends JpaRepository<ProductOrder, ProductOrderId> {

    @Query("SELECT po FROM ProductOrder po WHERE po.order.orderId = :orderId")
    List<ProductOrder> findByOrderId(@Param("orderId") Integer orderId);

    @Query("SELECT po FROM ProductOrder po WHERE po.product.productId = :productId")
    List<ProductOrder> findByProductId(@Param("productId") Integer productId);

    @Query("SELECT po FROM ProductOrder po WHERE po.product.productId = :productId AND po.order.orderId = :orderId")
    Optional<ProductOrder> findByProductIdAndOrderId(@Param("productId") Integer productId, @Param("orderId") Integer orderId);

    @Query("SELECT CASE WHEN COUNT(po) > 0 THEN true ELSE false END FROM ProductOrder po WHERE po.product.productId = :productId AND po.order.orderId = :orderId")
    boolean existsByProductIdAndOrderId(@Param("productId") Integer productId, @Param("orderId") Integer orderId);

    @Modifying
    @Query("DELETE FROM ProductOrder po WHERE po.product.productId = :productId AND po.order.orderId = :orderId")
    void deleteByProductIdAndOrderId(@Param("productId") Integer productId, @Param("orderId") Integer orderId);
}
