package org.orderpulse.orderpulsebackend.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")  // Using 'orders' because 'order' is a reserved SQL keyword
@Data  // Lombok annotation to generate getters, setters, toString, etc.
@Builder  // Enables builder pattern for creating orders
@NoArgsConstructor  // Required for JPA
@AllArgsConstructor  // Works with @Builder
public class Order {

    @Id  // Marks this as the primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)  // Auto-generates IDs
    private Long id;

    @Column(nullable = false)
    private String customerName;

    @Column(nullable = false)
    private String deliveryAddress;

    @Enumerated(EnumType.STRING)  // Store enum as STRING in database
    private OrderStatus status;

    @CreationTimestamp  // Automatically sets creation time
    private LocalDateTime createdAt;

    @UpdateTimestamp  // Automatically updates when order is modified
    private LocalDateTime updatedAt;

    private LocalDateTime estimatedDeliveryTime;

    @Column(columnDefinition = "TEXT")  // For longer text
    private String specialInstructions;

    private BigDecimal totalAmount;

}
