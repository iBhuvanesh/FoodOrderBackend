package com.order_service.service;

import com.order_service.dto.CreateOrderRequest;
import com.order_service.dto.OrderItemResponse;
import com.order_service.dto.OrderResponse;
import com.order_service.entity.Order;
import com.order_service.entity.OrderItem;
import com.order_service.entity.OrderStatus;
import com.order_service.exception.OrderNotFoundException;
import com.order_service.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private static final Logger log = LoggerFactory.getLogger(OrderService.class);

    // ================= CREATE ORDER =================

    public OrderResponse createOrder(CreateOrderRequest request) {

        log.info("Creating order for user {}", request.getUserId());

        double total = request.getItems()
                .stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        Order order = Order.builder()
                .userId(request.getUserId())
                .restaurantId(request.getRestaurantId())
                .totalAmount(total)
                .status(OrderStatus.CREATED)
                .createdAt(LocalDateTime.now())
                .build();

        // 🔥 Proper aggregate handling
        request.getItems().forEach(i -> {

            OrderItem item = OrderItem.builder()
                    .menuItemId(i.getMenuItemId())
                    .quantity(i.getQuantity())
                    .price(i.getPrice())
                    .build();

            order.addItem(item); // sets both sides
        });

        Order savedOrder = orderRepository.save(order);

        log.info("Order {} created successfully", savedOrder.getId());

        return mapResponse(savedOrder);
    }

    // ================= USER ORDERS =================

    public List<OrderResponse> getUserOrders(Long userId) {

        return orderRepository.findByUserId(userId)
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    // ================= UPDATE STATUS =================

    public OrderResponse updateStatus(Long orderId, OrderStatus status) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() ->
                        new OrderNotFoundException("Order not found with id " + orderId));

        order.setStatus(status);
        orderRepository.save(order);

        log.info("Order {} updated to {}", orderId, status);

        return mapResponse(order);
    }

    // ================= ADMIN VIEW =================

    public List<OrderResponse> getAllOrders() {

        return orderRepository.findAll()
                .stream()
                .map(this::mapResponse)
                .toList();
    }

    // ================= MAPPER =================

    private OrderResponse mapResponse(Order order) {

        List<OrderItemResponse> items = order.getItems()
                .stream()
                .map(i -> OrderItemResponse.builder()
                        .menuItemId(i.getMenuItemId())
                        .quantity(i.getQuantity())
                        .price(i.getPrice())
                        .build())
                .toList();

        return OrderResponse.builder()
                .id(order.getId())
                .userId(order.getUserId())
                .restaurantId(order.getRestaurantId())
                .totalAmount(order.getTotalAmount())
                .status(order.getStatus().name())
                .createdAt(order.getCreatedAt())
                .items(items)
                .build();
    }
}