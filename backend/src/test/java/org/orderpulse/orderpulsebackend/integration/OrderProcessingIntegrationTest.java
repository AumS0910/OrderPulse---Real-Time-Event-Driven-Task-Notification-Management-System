package org.orderpulse.orderpulsebackend.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.orderpulse.orderpulsebackend.config.TestConfig;
import org.orderpulse.orderpulsebackend.dto.OrderRequest;
import org.orderpulse.orderpulsebackend.dto.OrderResponse;
import org.orderpulse.orderpulsebackend.entity.Order;
import org.orderpulse.orderpulsebackend.entity.OrderStatus;
import org.orderpulse.orderpulsebackend.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.test.EmbeddedKafkaBroker;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.CompletableFuture;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration tests for the complete order processing flow.
 * Tests the interaction between REST API, database, and Kafka messaging.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Import(TestConfig.class)
@EmbeddedKafka
public class OrderProcessingIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Autowired
    private EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    private ConsumerFactory<String, String> consumerFactory;

    @Value("${spring.kafka.template.default-topic}")
    private String orderTopic;

    private Consumer<String, String> consumer;

    @BeforeEach
    void setUp() {
        orderRepository.deleteAll();
        consumer = consumerFactory.createConsumer();
        consumer.subscribe(List.of(orderTopic));
    }

    @AfterEach
    void tearDown() {
        consumer.close();
    }

    @Test
    void orderProcessingFlow_Success() throws Exception {
        // Create order request
        OrderRequest orderRequest = OrderRequest.builder()
            .customerName("John Doe")
            .productName("Test Product")
            .quantity(2)
            .totalAmount(BigDecimal.valueOf(100.00))
            .build();

        // Submit order through REST API
        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        // Extract order ID from response
        OrderResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            OrderResponse.class
        );
        Long orderId = response.getId();

        // Enhance order creation verification
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Order savedOrder = orderRepository.findById(orderId).orElseThrow();
            assertThat(savedOrder.getStatus()).isEqualTo(OrderStatus.NEW);
            assertThat(savedOrder.getCustomerName()).isEqualTo("John Doe");
            assertThat(savedOrder.getProductName()).isEqualTo("Test Product");
            assertThat(savedOrder.getQuantity()).isEqualTo(2);
            assertThat(savedOrder.getTotalAmount()).isEqualByComparingTo(BigDecimal.valueOf(100.00));
        });

        // Update order status
        mockMvc.perform(put("/api/orders/{id}/status", orderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"PROCESSING\"}"))
            .andExpect(status().isOk());

        // Verify status update in database
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            Order updatedOrder = orderRepository.findById(orderId).orElseThrow();
            assertThat(updatedOrder.getStatus()).isEqualTo(OrderStatus.PROCESSING);
        });

        // Enhance order retrieval verification
        mockMvc.perform(get("/api/orders/{id}", orderId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.id").value(orderId))
            .andExpect(jsonPath("$.customerName").value("John Doe"))
            .andExpect(jsonPath("$.productName").value("Test Product"))
            .andExpect(jsonPath("$.quantity").value(2))
            .andExpect(jsonPath("$.totalAmount").value(100.00))
            .andExpect(jsonPath("$.status").value("PROCESSING"));

        // Delete order
        mockMvc.perform(delete("/api/orders/{id}", orderId))
            .andExpect(status().isNoContent());

        // Verify order deletion
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            assertThat(orderRepository.findById(orderId)).isEmpty();
        });
    }

    @Test
    void orderProcessingFlow_ValidationFailure() throws Exception {
        // Create invalid order request
        OrderRequest invalidRequest = OrderRequest.builder()
            .customerName("")  // Empty customer name
            .productName("Test Product")
            .quantity(-1)      // Negative quantity
            .totalAmount(BigDecimal.valueOf(-100.00))  // Negative amount
            .build();

        // Submit invalid order and verify validation errors
        mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(invalidRequest)))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value("Validation failed for the request"))
            .andExpect(jsonPath("$.details").value(Matchers.allOf(
                containsString("customerName must not be empty"),
                containsString("quantity must be greater than 0"),
                containsString("totalAmount must be greater than 0")
            )));
    }

    @Test
    void orderProcessingFlow_NotFound() throws Exception {
        Long nonExistentOrderId = 999L;

        // Try to fetch non-existent order
        mockMvc.perform(get("/api/orders/{id}", nonExistentOrderId))
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("$.message").value("Order not found with ID: " + nonExistentOrderId));

        // Try to update non-existent order
        mockMvc.perform(put("/api/orders/{id}/status", nonExistentOrderId)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"PROCESSING\"}"))
            .andExpect(status().isNotFound());

        // Try to delete non-existent order
        mockMvc.perform(delete("/api/orders/{id}", nonExistentOrderId))
            .andExpect(status().isNotFound());
    }

    @Test
    void orderProcessingFlow_KafkaEventVerification() throws Exception {
        // Create and submit order
        OrderRequest orderRequest = OrderRequest.builder()
            .customerName("Jane Doe")
            .productName("Event Test Product")
            .quantity(1)
            .totalAmount(BigDecimal.valueOf(50.00))
            .build();

        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        OrderResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            OrderResponse.class
        );

        // Verify Kafka event for order creation
        ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
            consumer, orderTopic, 5000);
        assertThat(record).isNotNull();
        assertThat(record.value()).contains("ORDER_CREATED");
        assertThat(record.value()).contains(response.getId().toString());

        // Update order status
        mockMvc.perform(put("/api/orders/{id}/status", response.getId())
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"COMPLETED\"}"))
            .andExpect(status().isOk());

        // Verify Kafka event for status update
        record = KafkaTestUtils.getSingleRecord(consumer, orderTopic, 5000);
        assertThat(record).isNotNull();
        assertThat(record.value()).contains("ORDER_UPDATED");
        assertThat(record.value()).contains("COMPLETED");
    }

    @Test
    void orderProcessingFlow_ConcurrentOrders() throws Exception {
        int numberOfOrders = 5;
        CountDownLatch latch = new CountDownLatch(numberOfOrders);
        List<CompletableFuture<OrderResponse>> futures = new ArrayList<>();

        // Submit multiple orders concurrently
        for (int i = 0; i < numberOfOrders; i++) {
            OrderRequest orderRequest = OrderRequest.builder()
                .customerName("Customer " + i)
                .productName("Concurrent Product")
                .quantity(1)
                .totalAmount(BigDecimal.valueOf(75.00))
                .build();

            CompletableFuture<OrderResponse> future = CompletableFuture.supplyAsync(() -> {
                try {
                    MvcResult result = mockMvc.perform(post("/api/orders")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(orderRequest)))
                        .andExpect(status().isCreated())
                        .andReturn();

                    latch.countDown();
                    return objectMapper.readValue(
                        result.getResponse().getContentAsString(),
                        OrderResponse.class
                    );
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });

            futures.add(future);
        }

        // Wait for all orders to be submitted
        latch.await(10, TimeUnit.SECONDS);

        // Verify all orders were created successfully
        List<OrderResponse> responses = futures.stream()
            .map(CompletableFuture::join)
            .collect(Collectors.toList());

        assertThat(responses).hasSize(numberOfOrders);

        // Verify orders in database
        await().atMost(5, TimeUnit.SECONDS).untilAsserted(() -> {
            List<Order> savedOrders = orderRepository.findAll();
            assertThat(savedOrders).hasSize(numberOfOrders);
            assertThat(savedOrders)
                .extracting(Order::getProductName)
                .containsOnly("Concurrent Product");
        });

        // Verify Kafka events
        for (int i = 0; i < numberOfOrders; i++) {
            ConsumerRecord<String, String> record = KafkaTestUtils.getSingleRecord(
                consumer, orderTopic, 5000);
            assertThat(record).isNotNull();
            assertThat(record.value()).contains("ORDER_CREATED");
        }
    }

    @Test
    void orderProcessingFlow_FilterByCustomer() throws Exception {
        // Create multiple orders for different customers
        createOrder("Alice", "Product A", 1, BigDecimal.valueOf(100.00));
        createOrder("Bob", "Product B", 2, BigDecimal.valueOf(200.00));
        createOrder("Alice", "Product C", 3, BigDecimal.valueOf(300.00));

        // Test filtering by customer name
        mockMvc.perform(get("/api/orders/customer/Alice"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[*].customerName", everyItem(is("Alice"))));
    }

    @Test
    void orderProcessingFlow_FilterByStatus() throws Exception {
        // Create orders with different statuses
        Long order1Id = createOrder("Customer1", "Product X", 1, BigDecimal.valueOf(100.00));
        Long order2Id = createOrder("Customer2", "Product Y", 1, BigDecimal.valueOf(150.00));

        // Update one order to PROCESSING status
        mockMvc.perform(put("/api/orders/{id}/status", order1Id)
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\": \"PROCESSING\"}"))
            .andExpect(status().isOk());

        // Test filtering by NEW status
        mockMvc.perform(get("/api/orders/status/NEW"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(order2Id));

        // Test filtering by PROCESSING status
        mockMvc.perform(get("/api/orders/status/PROCESSING"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].id").value(order1Id));
    }

    private Long createOrder(String customerName, String productName, int quantity, BigDecimal amount) throws Exception {
        OrderRequest orderRequest = OrderRequest.builder()
            .customerName(customerName)
            .productName(productName)
            .quantity(quantity)
            .totalAmount(amount)
            .build();

        MvcResult result = mockMvc.perform(post("/api/orders")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(orderRequest)))
            .andExpect(status().isCreated())
            .andReturn();

        OrderResponse response = objectMapper.readValue(
            result.getResponse().getContentAsString(),
            OrderResponse.class
        );

        return response.getId();
    }
}