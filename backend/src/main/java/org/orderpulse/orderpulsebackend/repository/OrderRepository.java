package org.orderpulse.orderpulsebackend.repository;

import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    // Find orders by customer name
    List<Order> findByCustomerName(String customerName);
    
    // Find orders by status
    List<Order> findByStatus(OrderStatus status);
    
    // Find orders by customer name and status
    List<Order> findByCustomerNameAndStatus(String customerName, OrderStatus status);
}
