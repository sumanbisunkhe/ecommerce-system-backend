package com.example.ecommerce.repository;

import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {

    Page<Order> findByCreatedAtBetweenOrderByUpdatedAtDesc(LocalDateTime start, LocalDateTime end, Pageable pageable);

    Page<Order> findAllByOrderByUpdatedAtDesc(Pageable pageable);

    Page<Order> findOrdersByUserIdOrderByUpdatedAtDesc(Long userId, Pageable pageable);


    List<Order> user(User user);
}
