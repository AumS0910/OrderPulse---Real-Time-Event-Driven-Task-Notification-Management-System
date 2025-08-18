package org.orderpulse.orderpulsebackend.repository;

import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entity.
 * Extends JpaRepository to inherit basic CRUD operations and pagination support.
 *
 * Spring Data JPA will automatically implement this interface at runtime,
 * creating the necessary database queries based on method names.
 */
@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {

    /**
     * Find orders by customer name (case-insensitive partial match)
     */
    List<Order> findByCustomerNameContainingIgnoreCase(String customerName);

    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by status and creation date range
     */
    List<Order> findByStatusAndCreatedAtBetween(
        OrderStatus status,
        LocalDateTime startDate,
        LocalDateTime endDate
    );

    /**
     * Custom query to find orders with specific status and minimum amount
     */
    @Query("SELECT o FROM Order o WHERE o.status = :status AND o.totalAmount >= :minAmount")
    List<Order> findOrdersByStatusAndMinAmount(OrderStatus status, BigDecimal minAmount);
}