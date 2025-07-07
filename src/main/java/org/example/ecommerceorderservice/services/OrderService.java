package org.example.ecommerceorderservice.services;

import org.example.ecommerceorderservice.dtos.ProductStockRequest;
import org.example.ecommerceorderservice.dtos.ProductStockRequestItem;
import org.example.ecommerceorderservice.exceptions.NotFoundException;
import org.example.ecommerceorderservice.models.*;
import org.example.ecommerceorderservice.respositories.OrderRepository;
import org.flywaydb.core.internal.util.Pair;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
public class OrderService {

    private final OrderRepository orderRepository;

    private RestTemplate restTemplate;

    private RedissonClient redissonClient;

    public OrderService(OrderRepository orderRepository, RestTemplate restTemplate, RedissonClient redissonClient) {
        this.orderRepository = orderRepository;
        this.restTemplate = restTemplate;
        this.redissonClient = redissonClient;
    }

    public Long createOrder(Long userId, Long addressId, Double discount) {
        Order order = new Order();
        order.setUserId(userId);
        order.setAddressId(addressId);
        order.setDiscount(discount);

        // Fetch cart items from the cart service

        ResponseEntity<Cart> response = restTemplate.getForEntity("http://localhost:9001/cart/view/" + userId, Cart.class);
        Cart cart = response.getBody();

        List<Pair<Long, Integer>> productIds = new ArrayList<>();


        for(CartItem cartItem : cart.getCartItems()) {
            productIds.add(Pair.of(cartItem.getProductId(), cartItem.getQuantity()));
        }

        //Sort the product Ids TO AVOID DEADLOCKS

        productIds.sort(Comparator.comparing(Pair::getLeft));
        List<RLock> acquiredLocks = new ArrayList<>();
        try {
            for (Pair<Long, Integer> product : productIds) {
                RLock lock = redissonClient.getLock("lock:product:" + product.getLeft());
                lock.lock(10, TimeUnit.SECONDS);
                acquiredLocks.add(lock);
            }

            // Prepare request DTO
            List<ProductStockRequestItem> items = productIds.stream()
                    .map(pair -> new ProductStockRequestItem(pair.getLeft(), pair.getRight()))
                    .toList();
            ProductStockRequest request = new ProductStockRequest(items);

            // Check stock for all products
            ResponseEntity<Boolean> stockCheckResponse = restTemplate.postForEntity(
                    "http://localhost:9002/product/checkStock", request, Boolean.class);
            if (Boolean.FALSE.equals(stockCheckResponse.getBody())) {
                throw new RuntimeException("❌ One or more products are out of stock");
            }

            // Reduce stock for all products
            restTemplate.postForEntity(
                    "http://localhost:9002/product/reduceStock", request, Void.class);


            Order orderToSave = new Order();
            orderToSave.setUserId(userId);
            orderToSave.setAddressId(addressId);
            orderToSave.setDiscount(discount);
            orderToSave.setStatus(OrderStatus.PENDING);
            orderToSave.setPaymentStatus(PaymentStatus.PENDING);

            List<OrderItem> orderItemLists = new ArrayList<>();
            for (CartItem cartItem : cart.getCartItems()) {
                OrderItem orderItem = new OrderItem();
                orderItem.setProductId(cartItem.getProductId());
                orderItem.setQuantity(cartItem.getQuantity());
                orderItem.setPrice(cartItem.getPrice());

                orderItem.setOrder(orderToSave); // ✅ Set the parent order

                orderItemLists.add(orderItem);


                orderToSave.setOrderItems(orderItemLists);

                Order createdOrder = orderRepository.save(orderToSave);
                return createdOrder.getId();

            }
        }finally {
            for (RLock lock : acquiredLocks) {
                lock.unlock();
            }
        }
            return null; // This line should never be reached

    }

    public Order getOrderById(Long orderId) throws NotFoundException {
        Optional<Order> order= orderRepository.findById(orderId);
        if(order.isEmpty()) {
            throw new NotFoundException("Order not found with id: " + orderId);
        }
        System.out.println("Order found: " + order.get());
            return order.get();

    }

    public void updateOrderStatus(Long orderId, OrderStatus status) throws NotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setStatus(status);
            orderRepository.save(order);
        } else {
            throw new NotFoundException("Order not found with id: " + orderId);
        }
    }
    public void updatePaymentStatus(Long orderId, PaymentStatus paymentStatus) throws NotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            Order order = orderOptional.get();
            order.setPaymentStatus(paymentStatus);
            orderRepository.save(order);
        } else {
            throw new NotFoundException("Order not found with id: " + orderId);
        }
    }
    public void deleteOrder(Long orderId) throws NotFoundException {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isPresent()) {
            orderRepository.delete(orderOptional.get());
        } else {
            throw new NotFoundException("Order not found with id: " + orderId);
        }
    }
    public List<Order> getAllOrders() throws NotFoundException {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found");
        }
        return orders;
    }

    public List<Order> getOrdersByUserId(Long userId) throws NotFoundException {
        List<Order> orders = orderRepository.findByUserId(userId);
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found for user with id: " + userId);
        }
        return orders;
    }

    public List<Order> getOrdersByOrderStatus(OrderStatus status) throws NotFoundException {
        List<Order> orders = orderRepository.findAll();
        if (orders.isEmpty()) {
            throw new NotFoundException("No orders found with status: " + status);
        }
        return orders.stream()
                .filter(order -> order.getStatus() == status)
                .toList();
    }

}
