package com.cart_service.client;

import com.cart_service.dto.CreateOrderRequest;
import com.cart_service.dto.OrderResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "order-service")
public interface OrderClient {

    @PostMapping("/orders")
    OrderResponse createOrder(@RequestBody CreateOrderRequest request);
}