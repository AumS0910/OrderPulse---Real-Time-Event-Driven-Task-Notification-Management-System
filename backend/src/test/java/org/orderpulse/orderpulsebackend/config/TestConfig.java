package org.orderpulse.orderpulsebackend.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.utils.KafkaTestUtils;

import java.util.Map;

/**
 * Test configuration for integration tests.
 * Provides embedded Kafka broker and test-specific beans.
 */
@TestConfiguration
public class TestConfig {

    @Bean
    public EmbeddedKafkaBroker embeddedKafkaBroker() {
        return new EmbeddedKafkaBroker(1)
            .partitions(1)
            .topics("order-events");
    }

    @Bean
    public ProducerFactory<String, String> producerFactory(EmbeddedKafkaBroker broker) {
        Map<String, Object> props = KafkaTestUtils.producerProps(broker);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public ConsumerFactory<String, String> consumerFactory(EmbeddedKafkaBroker broker) {
        Map<String, Object> props = KafkaTestUtils.consumerProps("test-group", "true", broker);
        return new DefaultKafkaConsumerFactory<>(props);
    }
}