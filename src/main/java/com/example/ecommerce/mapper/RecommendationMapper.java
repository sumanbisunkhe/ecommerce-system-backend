package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.RecommendationDto;
import com.example.ecommerce.entity.Recommendation;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "user.id", target = "userId")
    RecommendationDto toDto(Recommendation entity);

    @Mapping(source = "productId", target = "product.id")
    @Mapping(source = "userId", target = "user.id")
    Recommendation toEntity(RecommendationDto dto);
}
