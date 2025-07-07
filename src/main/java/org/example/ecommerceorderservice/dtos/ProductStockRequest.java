package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class ProductStockRequest {
    private List<ProductStockRequestItem> items;
    // getters and setters

    public ProductStockRequest(List<ProductStockRequestItem> items) {
        this.items = items;
    }
}