package org.orderpulse.orderpulsebackend.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;


//What this does
//
//Creates a Kafka topic (order-events) when the app starts.
//
//Partitions = splits load across brokers (3 is fine for local dev).
//
//Replicas = copies of data (only 1 here for local dev).

@Configuration
public class KafkaTopicConfig {

    public static final String ORDER_TOPIC = "order-events";


    @Bean
    public NewTopic orderEvents() {
        return TopicBuilder
                .name(ORDER_TOPIC)
                .partitions(3)
                .replicas(1)
                .build();
    }
}
