package org.example.ecommerceorderservice.models;

public enum PaymentStatus {
    PENDING, // Payment is pending
    COMPLETED, // Payment has been completed successfully
    FAILED, // Payment has failed
    REFUNDED, // Payment has been refunded
    CANCELED // Payment has been canceled
}
