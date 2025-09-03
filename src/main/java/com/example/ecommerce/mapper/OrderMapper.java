package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.OrderDto;
import com.example.ecommerce.dto.OrderItemDto;
import com.example.ecommerce.entity.Order;
import com.example.ecommerce.entity.OrderItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderMapper {

    @Mapping(source = "user.id", target = "userId")
    OrderDto toDto(Order order);

    @Mapping(source = "userId", target = "user.id")
    Order toEntity(OrderDto dto);

    @Mapping(source = "product.id", target = "productId")
    OrderItemDto toItemDto(OrderItem item);

    @Mapping(source = "productId", target = "product.id")
    OrderItem toItemEntity(OrderItemDto dto);
}
