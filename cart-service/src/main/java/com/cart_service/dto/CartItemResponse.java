package com.cart_service.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long id;
    private Long restaurantId;
    private Long menuItemId;
    private Integer quantity;
    private Double price;
    private String menuItemName;
}