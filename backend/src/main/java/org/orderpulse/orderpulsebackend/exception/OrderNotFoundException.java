package org.orderpulse.orderpulsebackend.exception;

/**
 * Exception thrown when an order cannot be found in the system.
 * This is a runtime exception used to indicate that an order lookup operation failed.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    public OrderNotFoundException(Long orderId) {
        super("Order not found with ID: " + orderId);
    }
}