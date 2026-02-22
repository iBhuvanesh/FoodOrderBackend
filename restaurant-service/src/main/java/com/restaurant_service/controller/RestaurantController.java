package com.restaurant_service.controller;


import com.restaurant_service.service.RestaurantService;

import com.restaurant_service.dto.RestaurantRequest;
import com.restaurant_service.dto.RestaurantResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import com.restaurant_service.dto.*;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/restaurants")
@RequiredArgsConstructor
public class RestaurantController {

    private final RestaurantService service;

    @PostMapping
    public RestaurantResponse createRestaurant(@Valid @RequestBody RestaurantRequest request) {
        return service.createRestaurant(request);
    }

    @GetMapping
    public List<RestaurantResponse> getAllRestaurants() {
        return service.getAllRestaurants();
    }

    @GetMapping("/{id}")
    public RestaurantResponse getRestaurantById(@PathVariable("id") Long id) {
        return service.getRestaurantById(id);
    }

    @PostMapping("/{id}/menu")
    public MenuItemResponse addMenu(@PathVariable("id") Long id,
                                    @RequestBody MenuItemRequest request) {
        return service.addMenuItem(id, request);
    }

    @GetMapping("/{id}/menu")
    public List<MenuItemResponse> getMenu(@PathVariable("id") Long id) {
        return service.getMenu(id);
    }

    @GetMapping("/menu-item/{menuItemId}")
    public MenuItemResponse getMenuItemById(
            @PathVariable("menuItemId") Long menuItemId) {
        return service.getMenuItemById(menuItemId);
    }
}