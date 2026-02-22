package com.restaurant_service.service;


import com.restaurant_service.dto.MenuItemRequest;
import com.restaurant_service.dto.MenuItemResponse;
import com.restaurant_service.dto.RestaurantRequest;
import com.restaurant_service.dto.RestaurantResponse;
import com.restaurant_service.entity.MenuItem;
import com.restaurant_service.entity.Restaurant;
import com.restaurant_service.exception.RestaurantNotFoundException;
import com.restaurant_service.repository.MenuItemRepository;
import com.restaurant_service.repository.RestaurantRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final MenuItemRepository menuItemRepository;

    // ---------------- RESTAURANT ----------------

    public RestaurantResponse createRestaurant(RestaurantRequest request) {

        Restaurant restaurant = Restaurant.builder()
                .name(request.getName())
                .location(request.getLocation())
                .cuisineType(request.getCuisineType())
                .build();

        Restaurant saved = restaurantRepository.save(restaurant);

        return mapRestaurant(saved);
    }

    public List<RestaurantResponse> getAllRestaurants() {
        return restaurantRepository.findAll()
                .stream()
                .map(this::mapRestaurant)
                .collect(Collectors.toList());
    }

    public RestaurantResponse getRestaurantById(Long id) {
        Restaurant restaurant = restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found with id: " + id));
        return new RestaurantResponse(restaurant.getId(), restaurant.getName(), restaurant.getLocation(),restaurant.getCuisineType());
    }

    public Restaurant getRestaurantEntity(Long id) {
        return restaurantRepository.findById(id)
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant not found"));
    }

    // ---------------- MENU ----------------

    public MenuItemResponse addMenuItem(Long restaurantId,
                                        MenuItemRequest request) {

        Restaurant restaurant = getRestaurantEntity(restaurantId);

        MenuItem item = MenuItem.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .restaurant(restaurant)
                .build();

        MenuItem saved = menuItemRepository.save(item);

        return mapMenu(saved);
    }

    public List<MenuItemResponse> getMenu(Long restaurantId) {

        return menuItemRepository.findByRestaurantId(restaurantId)
                .stream()
                .map(this::mapMenu)
                .collect(Collectors.toList());
    }

    // ---------------- MAPPERS ----------------

    private RestaurantResponse mapRestaurant(Restaurant restaurant) {
        return RestaurantResponse.builder()
                .id(restaurant.getId())
                .name(restaurant.getName())
                .location(restaurant.getLocation())
                .cuisineType(restaurant.getCuisineType())
                .build();
    }

    private MenuItemResponse mapMenu(MenuItem item) {
        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .build();
    }

    public MenuItemResponse getMenuItemById(Long menuItemId) {
        MenuItem item = menuItemRepository.findById(menuItemId)
                .orElseThrow(() -> new RuntimeException("Menu item not found"));

        return MenuItemResponse.builder()
                .id(item.getId())
                .name(item.getName())
                .price(item.getPrice())
                .build();
    }
}