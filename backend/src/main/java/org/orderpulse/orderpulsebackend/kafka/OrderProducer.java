package org.orderpulse.orderpulsebackend.kafka;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.config.KafkaTopicConfig;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;


//What this does
//
//Uses KafkaTemplate to send messages to Kafka.
//
//Sends plain strings now (JSON format for orders).
//
//Will be triggered by service or controller when an order is created.

@Service
@RequiredArgsConstructor
public class OrderProducer {

    private final KafkaTemplate<String, String> kafkaTemplate;

    public void sendOrder(String orderJson) {
        kafkaTemplate.send(KafkaTopicConfig.ORDER_TOPIC, orderJson);
        System.out.println("Order sent to topic: " + orderJson);
    }
}
