package org.orderpulse.orderpulsebackend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

/**
 * Main application class for the OrderPulse Backend service.
 * 
 * @SpringBootApplication: This annotation combines:
 * - @Configuration: Tags the class as a source of bean definitions
 * - @EnableAutoConfiguration: Tells Spring Boot to auto-configure the application
 * - @ComponentScan: Tells Spring to look for other components in the same package
 *
 * @EnableJpaAuditing: Enables JPA Auditing which allows automatic population of
 * createdAt and updatedAt fields in our entities
 */
@SpringBootApplication
@EnableJpaAuditing
public class OrderPulseBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderPulseBackendApplication.class, args);
    }
}