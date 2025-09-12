package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.entity.Analytics;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnalyticsMapper {

    AnalyticsMapper INSTANCE = Mappers.getMapper(AnalyticsMapper.class);

    @Mapping(target = "newProducts", ignore = true)
    @Mapping(target = "popularProducts", ignore = true)
    @Mapping(target = "newCategories", ignore = true)
    AnalyticsDto toDTO(Analytics analytics);

    @Mapping(target = "createdAt", ignore = true)
    Analytics toEntity(AnalyticsDto analyticsDto);
}