package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceorderservice.models.Order;
import org.example.ecommerceorderservice.models.OrderStatus;
import org.example.ecommerceorderservice.models.PaymentStatus;

@Getter
@Setter
public class CreateOrderRequestDto {
    // ID of the user placing the order// ID of the cart associated with the order
    private Long addressId;
    private Double discount;// Shipping address for the order
// Status of the order (e.g., "PENDING", "COMPLETED", "CANCELLED")


}
