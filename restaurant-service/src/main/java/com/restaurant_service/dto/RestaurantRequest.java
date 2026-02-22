package com.restaurant_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RestaurantRequest {

    private String name;
    private String location;
    private String cuisineType;
}