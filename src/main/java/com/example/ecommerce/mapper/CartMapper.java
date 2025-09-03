package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.CartDto;
import com.example.ecommerce.dto.CartItemDto;
import com.example.ecommerce.entity.Cart;
import com.example.ecommerce.entity.CartItem;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartMapper {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "items", source = "items") // map Cart.items → CartDto.items
    CartDto toDto(Cart cart);

    @Mapping(target = "user.id", source = "userId")
    @Mapping(target = "items", ignore = true) // handled manually in service
    Cart toEntity(CartDto dto);

    // Mapping CartItem → CartItemDto
    @Mapping(target = "productId", source = "product.id")
    @Mapping(target = "productName", source = "product.name")
    CartItemDto toDto(CartItem item);

    @Mapping(target = "product.id", source = "productId")
    @Mapping(target = "cart", ignore = true) // set manually in service
    CartItem toEntity(CartItemDto dto);
}
