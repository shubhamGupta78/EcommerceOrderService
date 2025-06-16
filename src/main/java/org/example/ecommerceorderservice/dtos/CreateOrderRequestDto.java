package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceorderservice.models.Order;
import org.example.ecommerceorderservice.models.OrderStatus;
import org.example.ecommerceorderservice.models.PaymentStatus;

@Getter
@Setter
public class CreateOrderRequestDto {
    // ID of the user placing the order
    private Long cartId; // ID of the cart associated with the order
    private Long addressId;
    private Double discount;// Shipping address for the order
// Status of the order (e.g., "PENDING", "COMPLETED", "CANCELLED")

    public Order toOrder() {
        Order order = new Order();
        order.setCartId(this.cartId);
        order.setAddressId(this.addressId);
        order.setDiscount(this.discount != null ? this.discount : 0.0); // Default to 0.0 if discount is not provided
        // Default values for status and paymentStatus can be set here
        order.setStatus(OrderStatus.PENDING); // Assuming PENDING is a valid status
        order.setPaymentStatus(PaymentStatus.PENDING); // Assuming PENDING is a valid payment status
        return order;
    }
}
