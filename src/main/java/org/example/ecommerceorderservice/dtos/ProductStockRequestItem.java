package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ProductStockRequestItem {
    private Long productId;
    private Integer quantity;
    // constructor, getters, setters

    public ProductStockRequestItem(Long productId, Integer quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }
}