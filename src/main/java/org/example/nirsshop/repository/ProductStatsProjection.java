package org.example.nirsshop.repository;

public interface ProductStatsProjection {
    Integer getProductId();
    String getName();
    Double getRating();
    Long getTotalIncome();
    Long getCleanIncome();
    Long getTotalSold();
}
