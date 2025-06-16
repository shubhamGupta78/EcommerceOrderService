package org.example.ecommerceorderservice.models;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name="order_table")
public class Order extends  BaseModel {



    private Long userId;

    private Long addressId; // ID of the address where the order will be shipped

    private Long cartId; // ID of the cart associated with this order

    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    private double discount;

    private double finalPrice;

    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus paymentStatus;
}
