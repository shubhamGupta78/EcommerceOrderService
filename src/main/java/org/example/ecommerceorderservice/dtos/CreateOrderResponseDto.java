package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreateOrderResponseDto {
    public Long orderId;
    public String message;
}
