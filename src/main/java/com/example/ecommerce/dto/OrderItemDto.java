package com.example.ecommerce.dto;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;

@Data
@Builder
public class OrderItemDto {
    private Long productId;
    private Integer quantity;
    private BigDecimal price;
    private BigDecimal totalPrice;
}
