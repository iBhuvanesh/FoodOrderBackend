package com.order_service.controller;

import com.order_service.dto.CreateOrderRequest;
import com.order_service.dto.OrderResponse;
import com.order_service.entity.OrderStatus;
import com.order_service.service.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService service;

    // ✅ NEW — Get all orders (Admin) or user orders (User)
    @GetMapping
    public List<OrderResponse> getAllOrders() {
        return service.getAllOrders();
    }

    @PostMapping
    public OrderResponse create(@Valid @RequestBody CreateOrderRequest request) {
        return service.createOrder(request);
    }

    @GetMapping("/user/{userId}")
    public List<OrderResponse> getUserOrders(@PathVariable("userId") Long userId) {
        return service.getUserOrders(userId);
    }

    // ✅ FIXED — Removed @PreAuthorize (gateway handles role auth, not microservice)
    @PutMapping("/{orderId}/status")
    public OrderResponse updateStatus(@PathVariable("orderId") Long orderId,
                                      @RequestParam OrderStatus status) {
        return service.updateStatus(orderId, status);
    }
}