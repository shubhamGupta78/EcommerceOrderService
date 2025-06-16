package org.example.ecommerceorderservice.controllers;

import org.example.ecommerceorderservice.dtos.CreateOrderRequestDto;
import org.example.ecommerceorderservice.dtos.CreateOrderResponseDto;
import org.example.ecommerceorderservice.dtos.GetOrderDetailsResponseDto;
import org.example.ecommerceorderservice.dtos.UpdateOrderRequestDto;
import org.example.ecommerceorderservice.models.Order;
import org.example.ecommerceorderservice.models.OrderStatus;
import org.example.ecommerceorderservice.services.OrderService;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }


    @PostMapping("/create/{userId}")
    public ResponseEntity<CreateOrderResponseDto> createOrder(@PathVariable Long userId, CreateOrderRequestDto createOrderRequestDto) {

        Long orderId = orderService.createOrder(userId, createOrderRequestDto.toOrder());
        CreateOrderResponseDto createOrderResponseDto = new CreateOrderResponseDto();
        createOrderResponseDto.setOrderId(orderId);
        createOrderResponseDto.setMessage("Order created successfully");
        return ResponseEntity.ok(createOrderResponseDto);
    }

    @GetMapping("/view/{orderId}")
    public ResponseEntity<GetOrderDetailsResponseDto> viewOrder(Long orderId) {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(GetOrderDetailsResponseDto.fromOrder(order));
    }

    @PatchMapping("/update/{orderId}")
    public ResponseEntity<String> updateOrder(@PathVariable Long orderId, @RequestBody UpdateOrderRequestDto request) {
        orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok("Order status updated successfully");

    }

    @GetMapping("/user/{userId}/")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewUserOrders(@RequestParam Long userId) {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);
    }


    @GetMapping("view/all")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewAllOrders() {
        List<Order> orders = orderService.getAllOrders();
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);

    }

    @GetMapping("/view/orders")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewOrdersByStatus(@RequestParam String status) {
        List<Order> orders = orderService.getOrdersByOrderStatus(OrderStatus.valueOf(status));
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);
    }
}