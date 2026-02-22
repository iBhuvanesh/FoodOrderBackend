package com.restaurant_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MenuItemRequest {

    private String name;
    private String description;
    private Double price;
}