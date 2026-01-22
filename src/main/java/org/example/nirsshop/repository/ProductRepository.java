package org.example.nirsshop.repository;

import org.example.nirsshop.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Integer>, JpaSpecificationExecutor<Product> {
    Optional<Product> findByArticle(String article);

    @Query(value = """
    SELECT 
        product_id as productId,
        name,
        article,
        price,
        rating,
        total_sold as totalSold
    FROM top_selling_products
    ORDER BY total_sold DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<PopularProductProjection> findTopSellingProducts(@Param("limit") int limit);

    @Query(value = """
    SELECT 
        product_id as productId,
        name,
        total_income as totalIncome,
        total_sold as totalSold
    FROM product_stats
    ORDER BY total_income DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<TopIncomeProductProjection> findTopIncomeProducts(@Param("limit") int limit);

    @Query(value = """
    SELECT 
        product_id as productId,
        name,
        clean_income as cleanIncome,
        total_sold as totalSold
    FROM product_stats
    ORDER BY clean_income DESC
    LIMIT :limit
    """, nativeQuery = true)
    List<TopCleanIncomeProductProjection> findTopCleanIncomeProducts(@Param("limit") int limit);

    @Query("""
    SELECT ps.productId as productId,
           ps.name as name,
           ps.rating as rating,
           ps.totalIncome as totalIncome,
           ps.cleanIncome as cleanIncome,
           ps.totalSold as totalSold
    FROM ProductStats ps
    ORDER BY 
        CASE WHEN :orderBy = 'totalIncome' THEN ps.totalIncome END DESC,
        CASE WHEN :orderBy = 'cleanIncome' THEN ps.cleanIncome END DESC,
        CASE WHEN :orderBy = 'totalSold' THEN ps.totalSold END DESC,
        CASE WHEN :orderBy = 'rating' THEN ps.rating END DESC
    LIMIT :limit
""")
    List<ProductStatsProjection> getProductStats(@Param("limit") int limit, @Param("orderBy") String orderBy);
    List<Product> findByNameContainingIgnoreCase(String name);
}

