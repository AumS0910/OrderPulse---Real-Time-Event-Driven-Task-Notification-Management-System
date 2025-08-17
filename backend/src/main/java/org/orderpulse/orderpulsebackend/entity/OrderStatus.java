package org.orderpulse.orderpulsebackend.entity;

/**
 * Enumeration of possible order statuses in the system.
 * Using an enum ensures type safety and provides a clear set of valid states.
 */
public enum OrderStatus {
    PENDING,         // Initial status when order is first created
    CONFIRMED,       // Order has been validated and confirmed
    PREPARING,       // Kitchen/System is preparing the order
    READY,           // Order is ready for delivery/pickup
    IN_TRANSIT,      // Order is being delivered
    DELIVERED,       // Order has been successfully delivered
    CANCELLED,       // Order was cancelled
    REFUNDED         // Order was refunded
}