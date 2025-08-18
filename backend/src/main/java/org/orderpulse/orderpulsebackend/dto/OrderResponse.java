package org.orderpulse.orderpulsebackend.dto;

import lombok.Data;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for order responses in the REST API.
 * Contains all necessary order information for client consumption.
 */
@Data
public class OrderResponse {
    private Long id;
    private String customerName;
    private BigDecimal totalAmount;
    private String description;
    private OrderStatus status;
    private LocalDateTime createdDate;
    private LocalDateTime lastModifiedDate;
    private Integer version;
}