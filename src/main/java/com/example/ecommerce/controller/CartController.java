package com.example.ecommerce.controller;

import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.service.CartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/carts")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @PostMapping("/create/{userId}")
    public ResponseEntity<ApiResponse<CartDto>> createCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart created", cartService.createCart(userId)));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApiResponse<CartDto>> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(ApiResponse.success("Cart retrieved", cartService.getCartByUserId(userId)));
    }

    @PostMapping("/{userId}/add/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> addProduct(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product added to cart", cartService.addProductToCart(userId, productId)));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<ApiResponse<CartDto>> removeProduct(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(ApiResponse.success("Product removed from cart", cartService.removeProductFromCart(userId, productId)));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<ApiResponse<Void>> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok(ApiResponse.success("Cart cleared", null));
    }
}
