package org.orderpulse.orderpulsebackend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * Kafka Topic Configuration
 *
 * This class configures Kafka topics for the application.
 * Kafka topics are channels through which messages are published and consumed.
 *
 * Key concepts:
 * - Topic: A category/feed name to which records are published
 * - Partitions: Topics are split into partitions for scalability
 * - Replicas: Copies of partitions for fault tolerance
 */
@Configuration
public class KafkaTopicConfig {

    /**
     * Name of the topic for order events.
     * This topic will handle all order-related messages (creation, updates, etc.)
     */
    public static final String ORDER_TOPIC = "order-events";

    /**
     * Creates a Kafka topic for order events.
     *
     * @return NewTopic - Configured topic with:
     * - 3 partitions for parallel processing
     * - 1 replica for development (increase for production)
     */
    @Bean
    public NewTopic orderTopic() {
        return TopicBuilder.name(ORDER_TOPIC)
                .partitions(3)  // Number of partitions for scalability
                .replicas(1)    // Number of replicas (1 for dev, more for prod)
                .build();
    }
}