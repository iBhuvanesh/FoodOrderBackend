package com.cart_service.client;

import com.cart_service.dto.MenuItemResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "restaurant-service")
public interface RestaurantClient {

    @GetMapping("/restaurants/menu-item/{menuItemId}")
    MenuItemResponse getMenuItem(
            @PathVariable("menuItemId") Long menuItemId);
}