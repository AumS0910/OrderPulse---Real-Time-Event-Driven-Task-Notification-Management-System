package org.orderpulse.orderpulsebackend.entity;

public enum OrderStatus {

    PLACED,          // Initial status when order is created
    CONFIRMED,       // Order is confirmed by the system
    PREPARING,       // Order is being prepared
    OUT_FOR_DELIVERY,// Order is out for delivery
    DELIVERED,       // Order has been delivered
    CANCELLED        // Order was cancelled
}
