package com.cart_service.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddToCartRequest {
    @NotNull
    private Long userId;
    @NotNull
    private Long restaurantId;
    @NotNull
    private Long menuItemId;

    private Integer quantity;
    @NotNull
    private Double price;
}