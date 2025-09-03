package com.example.ecommerce.service;

import com.example.ecommerce.dto.OrderDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    OrderDto createOrder(OrderDto dto);
    OrderDto getOrderById(Long id);
    Page<OrderDto> getAllOrders(String filter, Pageable pageable);
    OrderDto updateOrderStatus(Long id, String status);
    void deleteOrder(Long id);
}
