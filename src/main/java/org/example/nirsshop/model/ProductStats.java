package org.example.nirsshop.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.Immutable;

import java.math.BigDecimal;

@Entity
@Table(name = "product_stats")
@Immutable // Materialized view - только для чтения
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStats {

    @Id
    @Column(name = "product_id")
    private Integer productId;

    @Column(name = "name")
    private String name;

    @Column(name = "rating")
    private Double rating;

    @Column(name = "total_income")
    private BigDecimal totalIncome;

    @Column(name = "clean_income")
    private BigDecimal cleanIncome;

    @Column(name = "total_sold")
    private Long totalSold;
}

