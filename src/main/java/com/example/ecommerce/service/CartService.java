package com.example.ecommerce.service;

import com.example.ecommerce.dto.CartDto;

public interface CartService {

    CartDto createCart(Long userId);

    CartDto getCartByUserId(Long userId);

    CartDto addProductToCart(Long userId, Long productId);

    CartDto removeProductFromCart(Long userId, Long productId);

    void clearCart(Long userId);
}
