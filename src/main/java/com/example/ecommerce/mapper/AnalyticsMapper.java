package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.AnalyticsDto;
import com.example.ecommerce.entity.Analytics;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface AnalyticsMapper {
    AnalyticsMapper INSTANCE = Mappers.getMapper(AnalyticsMapper.class);

    AnalyticsDto toDto(Analytics entity);
    Analytics toEntity(AnalyticsDto dto);
}
