package org.orderpulse.orderpulsebackend.service.impl;

import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;
import org.orderpulse.orderpulsebackend.kafka.OrderProducer;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.orderpulse.orderpulsebackend.service.OrderService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Implementation of the OrderService interface that handles order management operations.
 * This service implements transaction management and integrates with Kafka for event publishing.
 */
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderProducer orderProducer;

    /**
     * Creates a new order and publishes an event to Kafka.
     * The entire operation is transactional to ensure data consistency.
     *
     * @param order The order to be created
     * @return The persisted order with generated ID and audit fields
     */
    @Override
    @Transactional
    public Order createOrder(Order order) {
        // Set initial status if not provided
        if (order.getStatus() == null) {
            order.setStatus(OrderStatus.PENDING);
        }

        // Save the order to the database
        Order savedOrder = orderRepository.save(order);

        // Publish order created event to Kafka
        orderProducer.sendMessage(savedOrder);

        return savedOrder;
    }

    /**
     * Retrieves an order by its ID.
     * Throws OrderNotFoundException if the order doesn't exist.
     *
     * @param orderId The ID of the order to retrieve
     * @return The found order
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    @Override
    @Transactional(readOnly = true)
    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderNotFoundException(orderId));
    }

    /**
     * Updates the status of an existing order and publishes an event to Kafka.
     * The entire operation is transactional to ensure data consistency.
     *
     * @param orderId The ID of the order to update
     * @param newStatus The new status to set
     * @return The updated order
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    @Override
    @Transactional
    public Order updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        order.setStatus(newStatus);

        // Save the updated order
        Order updatedOrder = orderRepository.save(order);

        // Publish order updated event to Kafka
        orderProducer.sendMessage(updatedOrder);

        return updatedOrder;
    }

    /**
     * Retrieves all orders for a specific customer.
     *
     * @param customerName The name of the customer
     * @return List of orders belonging to the customer
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByCustomer(String customerName) {
        return orderRepository.findByCustomerName(customerName);
    }

    /**
     * Retrieves all orders with a specific status.
     *
     * @param status The order status to filter by
     * @return List of orders with the specified status
     */
    @Override
    @Transactional(readOnly = true)
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    /**
     * Deletes an order and publishes a deletion event to Kafka.
     * The entire operation is transactional to ensure data consistency.
     *
     * @param orderId The ID of the order to delete
     * @throws OrderNotFoundException if no order exists with the given ID
     */
    @Override
    @Transactional
    public void deleteOrder(Long orderId) {
        // Verify the order exists before deletion
        Order order = getOrderById(orderId);

        // Delete the order
        orderRepository.deleteById(orderId);

        // Publish order deleted event to Kafka
        orderProducer.sendMessage(order);
    }
}