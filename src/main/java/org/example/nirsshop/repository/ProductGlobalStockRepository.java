package org.example.nirsshop.repository;

import org.example.nirsshop.model.ProductGlobalStock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

public interface ProductGlobalStockRepository extends JpaRepository<ProductGlobalStock, Integer> {
}
