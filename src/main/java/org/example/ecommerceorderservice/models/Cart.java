package org.example.ecommerceorderservice.models;

import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class Cart extends BaseModel {

    private Long userId;

    private List<CartItem> cartItems;

    private Double totalPrice;

    private String status; // e.g., "active", "checked_out", "cancelled"

}
