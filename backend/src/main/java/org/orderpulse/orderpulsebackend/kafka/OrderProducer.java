package org.orderpulse.orderpulsebackend.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * Kafka Producer for Order events.
 * Responsible for publishing order events to Kafka topics.
 *
 * Uses:
 * - KafkaTemplate for reliable message publishing
 * - ObjectMapper for JSON serialization
 * - Slf4j for logging
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Publishes an order event to Kafka.
     * Serializes the order to JSON and sends it to the order-events topic.
     *
     * @param order The order to publish
     * @param eventType The type of event (CREATE, UPDATE, etc.)
     */
    public void publishOrderEvent(Order order, String eventType) {
        try {
            // Create an event object with metadata
            OrderEvent event = OrderEvent.builder()
                .orderId(order.getId())
                .eventType(eventType)
                .orderData(order)
                .timestamp(LocalDateTime.now())
                .build();

            // Convert to JSON
            String eventJson = objectMapper.writeValueAsString(event);

            // Publish to Kafka
            kafkaTemplate.send("order-events", String.valueOf(order.getId()), eventJson);
            
            log.info("Published {} event for order ID: {}", eventType, order.getId());
        } catch (Exception e) {
            log.error("Failed to publish order event: {}", e.getMessage(), e);
            throw new OrderEventPublishException("Failed to publish order event", e);
        }
    }
}