package org.orderpulse.orderpulsebackend.service;

import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.event.OrderEvent;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

/**
 * Service class for consuming order events from Kafka.
 * Processes different types of order events and performs appropriate actions.
 */
@Slf4j
@Service
public class OrderConsumerService {

    /**
     * Listens for order events on the specified Kafka topic.
     * Processes events based on their type (CREATE, UPDATE, DELETE).
     *
     * @param orderEvent The order event received from Kafka
     */
    @KafkaListener(
        topics = "${spring.kafka.topic.name}",
        groupId = "${spring.kafka.consumer.group-id}",
        containerFactory = "orderKafkaListenerContainerFactory"
    )
    public void consumeOrderEvent(OrderEvent orderEvent) {
        log.info("Order event received -> {}", orderEvent);

        switch (orderEvent.getEventType().toUpperCase()) {
            case "CREATE" -> processOrderCreation(orderEvent);
            case "UPDATE" -> processOrderUpdate(orderEvent);
            case "DELETE" -> processOrderDeletion(orderEvent);
            default -> log.warn("Unknown event type: {}", orderEvent.getEventType());
        }
    }

    /**
     * Processes order creation events.
     * Can be extended to include additional business logic like notifications.
     */
    private void processOrderCreation(OrderEvent orderEvent) {
        log.info("Processing order creation: {}", orderEvent.getOrder().getId());
        // Add business logic for order creation event
        // Example: Send notification to fulfillment system
    }

    /**
     * Processes order update events.
     * Can be extended to include additional business logic like status updates.
     */
    private void processOrderUpdate(OrderEvent orderEvent) {
        log.info("Processing order update: {}", orderEvent.getOrder().getId());
        // Add business logic for order update event
        // Example: Update inventory system
    }

    /**
     * Processes order deletion events.
     * Can be extended to include additional business logic like cleanup tasks.
     */
    private void processOrderDeletion(OrderEvent orderEvent) {
        log.info("Processing order deletion: {}", orderEvent.getOrder().getId());
        // Add business logic for order deletion event
        // Example: Archive order data
    }
}