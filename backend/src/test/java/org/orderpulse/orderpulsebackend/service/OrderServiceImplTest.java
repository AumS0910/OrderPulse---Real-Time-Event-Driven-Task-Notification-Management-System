package org.orderpulse.orderpulsebackend.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.exception.OrderNotFoundException;
import org.orderpulse.orderpulsebackend.kafka.OrderProducer;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.orderpulse.orderpulsebackend.service.impl.OrderServiceImpl;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Unit tests for OrderServiceImpl class.
 * Tests cover all major functionality including CRUD operations,
 * business logic, and integration with Kafka producer.
 */
@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private OrderProducer orderProducer;

    @InjectMocks
    private OrderServiceImpl orderService;

    private Order testOrder;

    @BeforeEach
    void setUp() {
        testOrder = new Order();
        testOrder.setId(1L);
        testOrder.setCustomerName("Test Customer");
        testOrder.setTotalAmount(new BigDecimal("100.00"));
        testOrder.setStatus(OrderStatus.PENDING);
    }

    /**
     * Test successful order creation with proper Kafka event publishing
     */
    @Test
    void createOrder_ShouldSaveAndPublishEvent() {
        // Arrange
        when(orderRepository.save(any(Order.class))).thenReturn(testOrder);
        doNothing().when(orderProducer).sendMessage(any(Order.class));

        // Act
        Order createdOrder = orderService.createOrder(testOrder);

        // Assert
        assertNotNull(createdOrder);
        assertEquals(testOrder.getCustomerName(), createdOrder.getCustomerName());
        assertEquals(OrderStatus.PENDING, createdOrder.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(orderProducer).sendMessage(any(Order.class));
    }

    /**
     * Test successful retrieval of an existing order
     */
    @Test
    void getOrderById_ShouldReturnOrder() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));

        // Act
        Order foundOrder = orderService.getOrderById(1L);

        // Assert
        assertNotNull(foundOrder);
        assertEquals(testOrder.getId(), foundOrder.getId());
        verify(orderRepository).findById(1L);
    }

    /**
     * Test order not found scenario
     */
    @Test
    void getOrderById_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.getOrderById(999L);
        });
        verify(orderRepository).findById(999L);
    }

    /**
     * Test successful order status update with Kafka event publishing
     */
    @Test
    void updateOrderStatus_ShouldUpdateAndPublishEvent() {
        // Arrange
        Order updatedOrder = new Order();
        updatedOrder.setId(1L);
        updatedOrder.setStatus(OrderStatus.COMPLETED);
        
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        when(orderRepository.save(any(Order.class))).thenReturn(updatedOrder);

        // Act
        Order result = orderService.updateOrderStatus(1L, OrderStatus.COMPLETED);

        // Assert
        assertEquals(OrderStatus.COMPLETED, result.getStatus());
        verify(orderRepository).save(any(Order.class));
        verify(orderProducer).sendMessage(any(Order.class));
    }

    /**
     * Test successful retrieval of orders by customer name
     */
    @Test
    void getOrdersByCustomer_ShouldReturnList() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByCustomerName("Test Customer")).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByCustomer("Test Customer");

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals("Test Customer", result.get(0).getCustomerName());
        verify(orderRepository).findByCustomerName("Test Customer");
    }

    /**
     * Test successful retrieval of orders by status
     */
    @Test
    void getOrdersByStatus_ShouldReturnList() {
        // Arrange
        List<Order> orders = Arrays.asList(testOrder);
        when(orderRepository.findByStatus(OrderStatus.PENDING)).thenReturn(orders);

        // Act
        List<Order> result = orderService.getOrdersByStatus(OrderStatus.PENDING);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(OrderStatus.PENDING, result.get(0).getStatus());
        verify(orderRepository).findByStatus(OrderStatus.PENDING);
    }

    /**
     * Test successful order deletion with Kafka event publishing
     */
    @Test
    void deleteOrder_ShouldDeleteAndPublishEvent() {
        // Arrange
        when(orderRepository.findById(1L)).thenReturn(Optional.of(testOrder));
        doNothing().when(orderRepository).deleteById(1L);

        // Act
        orderService.deleteOrder(1L);

        // Assert
        verify(orderRepository).deleteById(1L);
        verify(orderProducer).sendMessage(any(Order.class));
    }

    /**
     * Test delete non-existent order scenario
     */
    @Test
    void deleteOrder_ShouldThrowException() {
        // Arrange
        when(orderRepository.findById(999L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(OrderNotFoundException.class, () -> {
            orderService.deleteOrder(999L);
        });
        verify(orderRepository, never()).deleteById(any());
        verify(orderProducer, never()).sendMessage(any());
    }
}