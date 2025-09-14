package com.example.ecommerce.dto;

import com.example.ecommerce.enums.OrderStatus;
import com.example.ecommerce.enums.PaymentStatus;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Set;

@Data
@Builder
public class OrderDto {
    private Long id;
    private Long userId;
    private Set<OrderItemDto> items;
    private OrderStatus status;
    private PaymentStatus paymentStatus;
    private String shippingAddress;
    private BigDecimal shippingCost;
    private BigDecimal subtotal;
    private BigDecimal totalAmount;
}
