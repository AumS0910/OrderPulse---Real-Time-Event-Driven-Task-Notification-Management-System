package org.orderpulse.orderpulsebackend.service;

import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;

import java.util.List;

/**
 * Service interface for managing orders in the OrderPulse system.
 * This interface defines the core business operations for order processing.
 */
public interface OrderService {

    /**
     * Creates a new order in the system.
     * This operation is transactional and will trigger a Kafka event upon successful creation.
     *
     * @param order The order to be created
     * @return The created order with generated ID and audit fields
     */
    Order createOrder(Order order);

    /**
     * Retrieves an order by its unique identifier.
     *
     * @param orderId The ID of the order to retrieve
     * @return The found order
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    Order getOrderById(Long orderId);

    /**
     * Updates the status of an existing order.
     * This operation is transactional and will trigger a Kafka event upon successful update.
     *
     * @param orderId The ID of the order to update
     * @param newStatus The new status to set
     * @return The updated order
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    Order updateOrderStatus(Long orderId, OrderStatus newStatus);

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerName The name of the customer
     * @return List of orders belonging to the customer
     */
    List<Order> getOrdersByCustomer(String customerName);

    /**
     * Retrieves all orders with a specific status.
     *
     * @param status The order status to filter by
     * @return List of orders with the specified status
     */
    List<Order> getOrdersByStatus(OrderStatus status);

    /**
     * Deletes an order from the system.
     * This operation is transactional and will trigger a Kafka event upon successful deletion.
     *
     * @param orderId The ID of the order to delete
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    void deleteOrder(Long orderId);
}