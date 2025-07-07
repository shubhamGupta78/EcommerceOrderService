package org.example.ecommerceorderservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter

public class CartItem extends BaseModel {

    private int quantity;

    private double price;

    private Long productId;// Assuming productId is a Long type, adjust as necessary
}
