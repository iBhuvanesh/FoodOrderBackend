package com.restaurant_service.controller;


import com.restaurant_service.dto.MenuItemRequest;
import com.restaurant_service.dto.MenuItemResponse;
import com.restaurant_service.dto.RestaurantRequest;
import com.restaurant_service.dto.RestaurantResponse;
import com.restaurant_service.service.RestaurantService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
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
    @PatchMapping("/{id}/menu/{menuItemId}")
    public MenuItemResponse updateMenuItem(@PathVariable("id") Long restaurantId,
                                           @PathVariable("menuItemId") Long menuItemId,
                                           @RequestBody MenuItemRequest request) {
        return service.updateMenuItem(restaurantId, menuItemId, request);
    }

    @DeleteMapping("/{id}/menu/{menuItemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteMenuItem(@PathVariable("id") Long restaurantId,
                               @PathVariable("menuItemId") Long menuItemId) {
        service.deleteMenuItem(restaurantId, menuItemId);
    }
}