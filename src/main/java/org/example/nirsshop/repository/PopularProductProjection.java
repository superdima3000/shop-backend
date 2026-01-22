package org.example.nirsshop.repository;

public interface PopularProductProjection {
    Integer getProductId();
    String getName();
    String getArticle();
    Integer getPrice();
    Double getRating();
    Long getTotalSold();
}
