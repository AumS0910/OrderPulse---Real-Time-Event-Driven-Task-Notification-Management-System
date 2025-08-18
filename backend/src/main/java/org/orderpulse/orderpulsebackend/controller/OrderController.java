package org.orderpulse.orderpulsebackend.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.dto.OrderResponse;
import org.orderpulse.orderpulsebackend.dto.OrderStatusUpdateRequest;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.service.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * REST Controller for handling order-related operations.
 * Provides endpoints for CRUD operations on orders.
 */
@RestController
@RequestMapping("/api/orders")
@Tag(name = "Order Management", description = "APIs for managing orders")
public class OrderController {

    private final OrderService orderService;

    /**
     * Creates a new order.
     * 
     * @param orderRequest The order details from the client
     * @return The created order with generated ID
     */
    @Operation(summary = "Create a new order",
               description = "Creates a new order with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Order created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OrderResponse createOrder(@Valid @RequestBody OrderRequest orderRequest) {
        Order order = new Order();
        order.setCustomerName(orderRequest.getCustomerName());
        order.setTotalAmount(orderRequest.getTotalAmount());
        order.setDescription(orderRequest.getDescription());

        Order savedOrder = orderService.createOrder(order);
        return new ResponseEntity<>(mapToResponse(savedOrder), HttpStatus.CREATED);
    }

    /**
     * Retrieves an order by its ID.
     * 
     * @param orderId The ID of the order to retrieve
     * @return The order if found
     */
    @Operation(summary = "Get order by ID",
               description = "Retrieves an order by its unique identifier")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order found"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @GetMapping("/{id}")
    public OrderResponse getOrder(@PathVariable Long id) {
        Order order = orderService.getOrderById(id);
        return ResponseEntity.ok(mapToResponse(order));
    }

    /**
     * Updates the status of an order.
     * 
     * @param id The ID of the order to update
     * @param request The new status details
     * @return The updated order
     */
    @Operation(summary = "Update order status",
               description = "Updates the status of an existing order")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Order status updated"),
        @ApiResponse(responseCode = "404", description = "Order not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status")
    })
    @PutMapping("/{id}/status")
    public OrderResponse updateOrderStatus(
        @PathVariable Long id,
        @Valid @RequestBody OrderStatusUpdateRequest request) {
        Order updatedOrder = orderService.updateOrderStatus(id, request.getNewStatus());
        return ResponseEntity.ok(mapToResponse(updatedOrder));
    }

    /**
     * Retrieves all orders for a specific customer.
     * 
     * @param customerName The name of the customer
     * @return List of orders for the customer
     */
    @Operation(summary = "Get orders by customer",
               description = "Retrieves all orders for a specific customer")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found")
    })
    @GetMapping("/customer/{customerName}")
    public List<OrderResponse> getOrdersByCustomer(@PathVariable String customerName) {
        List<Order> orders = orderService.getOrdersByCustomer(customerName);
        List<OrderResponse> responses = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Retrieves all orders with a specific status.
     * 
     * @param status The order status to filter by
     * @return List of orders with the specified status
     */
    @Operation(summary = "Get orders by status",
               description = "Retrieves all orders with a specific status")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Orders found")
    })
    @GetMapping("/status/{status}")
    public List<OrderResponse> getOrdersByStatus(@PathVariable OrderStatus status) {
        List<Order> orders = orderService.getOrdersByStatus(status);
        List<OrderResponse> responses = orders.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    /**
     * Deletes an order by its ID.
     * 
     * @param orderId The ID of the order to delete
     * @return No content on successful deletion
     */
    @Operation(summary = "Delete order",
               description = "Deletes an order by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Order deleted"),
        @ApiResponse(responseCode = "404", description = "Order not found")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteOrder(@PathVariable Long id) {
        orderService.deleteOrder(orderId);
        return ResponseEntity.noContent().build();
    }

    /**
     * Maps an Order entity to OrderResponse DTO.
     * 
     * @param order The order entity to map
     * @return The mapped OrderResponse
     */
    private OrderResponse mapToResponse(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setCustomerName(order.getCustomerName());
        response.setTotalAmount(order.getTotalAmount());
        response.setDescription(order.getDescription());
        response.setStatus(order.getStatus());
        response.setCreatedDate(order.getCreatedDate());
        response.setLastModifiedDate(order.getLastModifiedDate());
        response.setVersion(order.getVersion());
        return response;
    }
}