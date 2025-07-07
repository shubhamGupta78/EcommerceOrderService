package org.example.ecommerceorderservice.controllers;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.ecommerceorderservice.dtos.CreateOrderRequestDto;
import org.example.ecommerceorderservice.dtos.CreateOrderResponseDto;
import org.example.ecommerceorderservice.dtos.GetOrderDetailsResponseDto;
import org.example.ecommerceorderservice.dtos.UpdateOrderRequestDto;
import org.example.ecommerceorderservice.exceptions.NotFoundException;
import org.example.ecommerceorderservice.models.Order;
import org.example.ecommerceorderservice.models.OrderStatus;
import org.example.ecommerceorderservice.services.OrderService;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/order")
public class OrderController {
    private final OrderService orderService;

    private final RedisTemplate redisTemplate;

    private final ObjectMapper objectMapper;

    public OrderController(OrderService orderService,
                           RedisTemplate redisTemplate) {
        this.orderService = orderService;
        this.redisTemplate = redisTemplate;
        this.objectMapper = new ObjectMapper();
    }


    @PostMapping("/create/{userId}")
    public ResponseEntity<CreateOrderResponseDto> createOrder(@PathVariable Long userId, @RequestBody  CreateOrderRequestDto createOrderRequestDto,@RequestHeader(value = "Idempotency-Key", required = false) String idempotencyKey) throws JsonProcessingException {

        if(idempotencyKey==null || idempotencyKey.isEmpty()) {
            throw new IllegalArgumentException("Idempotency-Key header is required");
        }

        String cachedResponse = (String) redisTemplate.opsForValue().get(idempotencyKey);

        if(cachedResponse!= null) {
            // If the response is cached, return it
            return ResponseEntity.ok(objectMapper.convertValue(cachedResponse, CreateOrderResponseDto.class));
        }
        System.out.println(createOrderRequestDto.getAddressId()+ createOrderRequestDto.getDiscount());
        Long orderId = orderService.createOrder(userId,createOrderRequestDto.getAddressId(),createOrderRequestDto.getDiscount());
        CreateOrderResponseDto createOrderResponseDto = new CreateOrderResponseDto();
        createOrderResponseDto.setOrderId(orderId);
        createOrderResponseDto.setMessage("Order created successfully");

        // Cache the response with the idempotency key
        redisTemplate.opsForValue().set(idempotencyKey, objectMapper.writeValueAsString(createOrderResponseDto), Duration.ofMinutes(10));
        return ResponseEntity.ok(createOrderResponseDto);
    }

    @GetMapping("/view/{orderId}")
    public ResponseEntity<Order> viewOrder(@PathVariable Long orderId) throws NotFoundException {
        Order order = orderService.getOrderById(orderId);
        return ResponseEntity.ok(order);
    }

    @PatchMapping("/update/{orderId}")
    public ResponseEntity<String> updateOrder(@PathVariable Long orderId, @RequestBody UpdateOrderRequestDto request) throws NotFoundException {
        orderService.updateOrderStatus(orderId, request.getStatus());
        return ResponseEntity.ok("Order status updated successfully");

    }

    @GetMapping("/user/{userId}/")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewUserOrders(@RequestParam Long userId) throws NotFoundException {
        List<Order> orders = orderService.getOrdersByUserId(userId);
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);
    }


    @GetMapping("view/all")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewAllOrders() throws NotFoundException {
        List<Order> orders = orderService.getAllOrders();
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);

    }

    @GetMapping("/view/orders")
    public ResponseEntity<List<GetOrderDetailsResponseDto>> viewOrdersByStatus(@RequestParam String status) throws NotFoundException {
        List<Order> orders = orderService.getOrdersByOrderStatus(OrderStatus.valueOf(status));
        List<GetOrderDetailsResponseDto> orderDetailsList = new ArrayList<>();
        for (Order order : orders) {
            orderDetailsList.add(GetOrderDetailsResponseDto.fromOrder(order));
        }
        return ResponseEntity.ok(orderDetailsList);
    }
}