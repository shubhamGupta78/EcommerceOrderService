package org.example.ecommerceorderservice.models;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.databind.annotation.EnumNaming;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name="order_table")
public class Order extends  BaseModel {



    private Long userId;

    private Long addressId; // ID of the address where the order will be shipped

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<OrderItem> orderItems;// ID of the cart associated with this order

    @Enumerated(EnumType.ORDINAL)
    private OrderStatus status;

    private double discount;

    private double finalPrice;

    @Enumerated(EnumType.ORDINAL)
    private PaymentStatus paymentStatus;
}
