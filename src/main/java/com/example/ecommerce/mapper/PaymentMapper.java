package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.PaymentDto;
import com.example.ecommerce.entity.Payment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface PaymentMapper {

    @Mapping(source = "order.id", target = "orderId")
    PaymentDto toDto(Payment payment);

    @Mapping(source = "orderId", target = "order.id")
    Payment toEntity(PaymentDto dto);
}
