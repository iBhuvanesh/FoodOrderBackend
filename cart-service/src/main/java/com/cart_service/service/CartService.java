package com.cart_service.service;

import com.cart_service.client.OrderClient;
import com.cart_service.client.RestaurantClient;
import com.cart_service.dto.*;
import com.cart_service.entity.Cart;
import com.cart_service.entity.CartItem;
import com.cart_service.exception.CartNotFoundException;
import com.cart_service.repository.CartItemRepository;
import com.cart_service.repository.CartRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository itemRepository;
    private final RestaurantClient restaurantClient;
    private final OrderClient orderClient;

    // ================= ADD TO CART =================

    public CartResponse addToCart(AddToCartRequest request) {

        Cart cart = cartRepository.findByUserId(request.getUserId());

        if (cart == null) {
            cart = Cart.builder()
                    .userId(request.getUserId())
                    .build();
            cart = cartRepository.save(cart);
        }

        CartItem item = CartItem.builder()
                .restaurantId(request.getRestaurantId())
                .menuItemId(request.getMenuItemId())
                .quantity(request.getQuantity())
                .price(request.getPrice())
                .cart(cart)
                .build();

        itemRepository.save(item);

        return viewCart(request.getUserId());
    }

    // ================= VIEW CART =================

    public CartResponse viewCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null) {
            throw new CartNotFoundException("Cart not found");
        }

        List<CartItemResponse> items =
                cart.getItems()
                        .stream()
                        .map(i -> {

                            // 🔥 Enrich using Restaurant Service
                            MenuItemResponse menu =
                                    restaurantClient.getMenuItem(i.getMenuItemId());

                            return CartItemResponse.builder()
                                    .id(i.getId())
                                    .menuItemId(i.getMenuItemId())
                                    .restaurantId(i.getRestaurantId())
                                    .menuItemName(menu.getName())   // ✅ Added
                                    .quantity(i.getQuantity())
                                    .price(i.getPrice())
                                    .build();
                        })
                        .toList();

        Double totalPrice = items.stream()
                .mapToDouble(i -> i.getPrice() * i.getQuantity())
                .sum();

        return CartResponse.builder()
                .userId(userId)
                .items(items)
                .totalPrice(totalPrice)
                .build();
    }

    // ================= CHECKOUT =================

    public CheckoutResponse checkout(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        if (cart == null || cart.getItems().isEmpty()) {
            throw new CartNotFoundException("Cart is empty");
        }

        List<OrderItemRequest> items = cart.getItems()
                .stream()
                .map(i -> new OrderItemRequest(
                        i.getMenuItemId(),
                        i.getQuantity(),
                        i.getPrice()
                ))
                .toList();

        CreateOrderRequest orderRequest =
                new CreateOrderRequest(
                        userId,
                        cart.getItems().get(0).getRestaurantId(),
                        items
                );

        OrderResponse orderResponse =
                orderClient.createOrder(orderRequest);

        // Clear cart after successful order
        cart.getItems().clear();
        cartRepository.save(cart);

        return CheckoutResponse.builder()
                .message("Order placed successfully")
                .orderId(orderResponse.getId())
                .build();
    }
}