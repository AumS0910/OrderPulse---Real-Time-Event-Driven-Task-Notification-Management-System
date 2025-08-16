package org.orderpulse.orderpulsebackend.service;

import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;

import java.util.List;
import java.util.Optional;

public interface OrderService {
    Order createOrder(Order order);
    Order updateOrder(Long id, Order order);
    Optional<Order> getOrderById(Long id);
    List<Order> getAllOrders();
    List<Order> getOrdersByStatus(OrderStatus status);
    List<Order> getOrdersByCustomerName(String customerName);
    void deleteOrder(Long id);
}