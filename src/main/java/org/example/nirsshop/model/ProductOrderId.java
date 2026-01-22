package org.example.nirsshop.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ProductOrderId implements Serializable {

    private Integer productId;
    private Integer orderId;

    // equals/hashCode, getters/setters
}

