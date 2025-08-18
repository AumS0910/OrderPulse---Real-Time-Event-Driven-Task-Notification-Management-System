package org.orderpulse.orderpulsebackend.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.orderpulse.orderpulsebackend.entity.Order;

import java.time.LocalDateTime;

/**
 * Represents an order event in the system.
 * This class defines the structure of messages that will be sent through Kafka.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderEvent {
    private String eventId;          // Unique identifier for the event
    private String eventType;        // Type of event (CREATE, UPDATE, DELETE)
    private Order order;             // The order associated with this event
    private LocalDateTime timestamp; // When the event occurred
    private String message;          // Optional message providing additional context
}