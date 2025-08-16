package org.orderpulse.orderpulsebackend.kafka;

import jakarta.persistence.criteria.Order;
import lombok.extern.slf4j.Slf4j;
import org.orderpulse.orderpulsebackend.config.KafkaTopicConfig;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

//What this does
//
//Listens to order-events topic.
//
//groupId ensures multiple consumers share load.
//
//Processes the message (later, we’ll integrate DB + WebSocket here).

@Slf4j
@Service
public class OrderConsumer {

    @KafkaListener(topics = KafkaTopicConfig.ORDER_TOPIC, groupId = "orderpulse-consumers")
    public void consume(String message) {
        log.info("📥 Received order from Kafka: {}", message);
        // TODO: process the order (save to DB, notify WebSocket, etc.)
    }
}
