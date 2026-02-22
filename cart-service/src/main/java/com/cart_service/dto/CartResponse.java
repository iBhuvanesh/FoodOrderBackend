package com.cart_service.dto;

import lombok.*;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartResponse {

    private Long userId;
    private List<CartItemResponse> items;
    private  Double totalPrice;
}