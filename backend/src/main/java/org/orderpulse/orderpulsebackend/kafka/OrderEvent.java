package org.orderpulse.orderpulsebackend.kafka;

import lombok.Builder;
import lombok.Data;
import org.orderpulse.orderpulsebackend.entity.Order;

import java.time.LocalDateTime;

/**
 * Represents an order event in the system.
 * This class is used for Kafka messages to provide context about order changes.
 */
@Data
@Builder
public class OrderEvent {
    private Long orderId;
    private String eventType;  // CREATE, UPDATE, DELETE, etc.
    private Order orderData;
    private LocalDateTime timestamp;
}