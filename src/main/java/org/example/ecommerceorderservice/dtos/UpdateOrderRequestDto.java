package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceorderservice.models.OrderStatus;

@Getter
@Setter
public class UpdateOrderRequestDto {
    private OrderStatus status;
}
