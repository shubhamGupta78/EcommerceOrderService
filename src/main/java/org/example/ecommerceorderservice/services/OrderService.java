package org.example.ecommerceorderservice.services;

import org.example.ecommerceorderservice.models.*;
import org.example.ecommerceorderservice.respositories.OrderRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private RestTemplate restTemplate;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
    }

    public Long createOrder(Long userId,Order order) {


        Order orderToSave = new Order();
        orderToSave.setUserId(order.getUserId());
        orderToSave.setAddressId(order.getAddressId());
        orderToSave.setCartId(order.getCartId());
        order.setDiscount(order.getDiscount());
        orderToSave.setStatus(OrderStatus.PENDING);
        orderToSave.setPaymentStatus(PaymentStatus.PENDING);
        Order createdOrder = orderRepository.save(orderToSave);
        return createdOrder.getId();


    }

    public Order getOrderById(Long orderId) {
        Optional<Order> order= orderRepository.findById(orderId);
        if (order.isPresent()) {
            return order.get();
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }

    }

    public void updateOrderStatus(Long orderId, OrderStatus status) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }
    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setPaymentStatus(paymentStatus);
            orderRepository.save(order);
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }
    public void deleteOrder(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            orderRepository.delete(orderOptional.get());
        } else {
            throw new RuntimeException("Order not found with id: " + orderId);
        }
    }
    public List<Order> getAllOrders() {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found");
        }
        return orders;
    }

    public List<Order> getOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found for user with id: " + userId);
        }
        return orders;
    }
    public List<Order> getOrdersByOrderStatus(OrderStatus status) {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new RuntimeException("No orders found with status: " + status);
        }
        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

}
