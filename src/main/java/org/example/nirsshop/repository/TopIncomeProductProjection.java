package org.example.nirsshop.repository;

public interface TopIncomeProductProjection {
    Integer getProductId();
    String getName();
    Long getTotalIncome();
    Long getTotalSold();
}
