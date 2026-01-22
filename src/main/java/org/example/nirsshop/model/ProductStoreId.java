package org.example.nirsshop.model;

import jakarta.persistence.Embeddable;
import lombok.Data;

import java.io.Serializable;

@Embeddable
@Data
public class ProductStoreId implements Serializable {

    private Integer productId;
    private Integer storeId;

    // equals/hashCode, getters/setters
}

