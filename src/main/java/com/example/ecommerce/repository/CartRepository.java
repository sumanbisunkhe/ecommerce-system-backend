package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByUserId(Long userId);

    Optional<Cart> findByUser(User user);
}
