package org.orderpulse.orderpulsebackend.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Order Entity - Core domain object representing an order in the system.
 *
 * This entity is designed to work with both JPA (for database persistence) and
 * Kafka (for event streaming). When an order is created or updated, it will be:
 * 1. Saved to the database through JPA
 * 2. Published to Kafka as an event for other services to consume
 *
 * Key annotations:
 * @Entity - Marks this as a JPA entity for database mapping
 * @EntityListeners - Enables automatic date auditing
 * @Table - Specifies the database table name
 * @Data - Lombok annotation for getters, setters, equals, hashCode, toString
 * @Builder - Enables builder pattern for object creation
 */
@Entity
@Table(name = "orders")
@EntityListeners(AuditingEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Customer name is required")
    private String customerName;

    @NotNull(message = "Total amount is required")
    @Positive(message = "Total amount must be positive")
    private BigDecimal totalAmount;

    @NotEmpty(message = "Order items are required")
    @Column(columnDefinition = "TEXT")
    private String items;  // JSON string of order items

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;

    private String notes;

    /**
     * This version field is crucial for optimistic locking.
     * It helps prevent concurrent modifications to the same order.
     */
    @Version
    private Long version;
}