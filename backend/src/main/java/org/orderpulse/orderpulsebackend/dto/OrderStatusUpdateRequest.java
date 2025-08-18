package org.orderpulse.orderpulsebackend.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;

/**
 * DTO for order status update requests.
 * Ensures status is provided for updates.
 */
@Data
public class OrderStatusUpdateRequest {

    @NotNull(message = "New status is required")
    private OrderStatus newStatus;
}