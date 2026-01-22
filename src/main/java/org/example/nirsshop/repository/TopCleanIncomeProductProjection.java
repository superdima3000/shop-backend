package org.example.nirsshop.repository;

public interface TopCleanIncomeProductProjection {
    Integer getProductId();
    String getName();
    Long getCleanIncome();
    Long getTotalSold();
}