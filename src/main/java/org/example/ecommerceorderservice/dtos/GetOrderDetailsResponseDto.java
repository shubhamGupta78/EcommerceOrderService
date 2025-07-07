package org.example.ecommerceorderservice.dtos;

import lombok.Getter;
import lombok.Setter;
import org.example.ecommerceorderservice.models.Order;

@Getter
@Setter

public class GetOrderDetailsResponseDto {
    private Long orderId;
    private String orderStatus;
    private Long userId;
    private Long cartId;
    private Long addressId;

    public static GetOrderDetailsResponseDto fromOrder(Order order) {
        GetOrderDetailsResponseDto orderDetailsResponseDto = new GetOrderDetailsResponseDto();
        orderDetailsResponseDto.setOrderId(order.getId());
        orderDetailsResponseDto.setOrderStatus(String.valueOf(order.getStatus()));
        orderDetailsResponseDto.setUserId(order.getUserId());
        orderDetailsResponseDto.setAddressId(order.getAddressId());
        return orderDetailsResponseDto;
    }

    // Additional fields can be added as needed
}
