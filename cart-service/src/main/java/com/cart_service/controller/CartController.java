package com.cart_service.controller;

import com.cart_service.dto.AddToCartRequest;
import com.cart_service.dto.CartResponse;
import com.cart_service.dto.CheckoutResponse;
import com.cart_service.service.CartService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService service;

    @PostMapping
    public CartResponse add(@Valid @RequestBody AddToCartRequest request) {
        return service.addToCart(request);
    }

    @GetMapping("/{userId}")
    public CartResponse view(@PathVariable("userId") Long userId) {
        return service.viewCart(userId);
    }

    @PostMapping("/{userId}/checkout")
    public CheckoutResponse checkout(@PathVariable("userId") Long userId) {
        return service.checkout(userId);
    }
}