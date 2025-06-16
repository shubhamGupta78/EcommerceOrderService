package org.example.ecommerceorderservice.models;

public enum OrderStatus {
    PENDING, // Order has been created but not yet processed
    PROCESSING, // Order is being processed
    SHIPPED, // Order has been shipped
    DELIVERED, // Order has been delivered to the customer
    CANCELED, // Order has been canceled by the user or the system
    RETURNED // Order has been returned by the customer
}
