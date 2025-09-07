package com.example.ecommerce.mapper;

import com.example.ecommerce.dto.UpdateUserDto;
import com.example.ecommerce.entity.User;
import com.example.ecommerce.dto.UserDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // Map User -> UserDto
    // Ignore any fields in User that are not in UserDto
    @Mapping(target = "password", ignore = true) // UserDto has no password
    @Mapping(target = "roles", source = "roles") // Map roles
    @Mapping(target = "isActive", source = "active")
    UserDto toDto(User user);

    UpdateUserDto toUpdateUserDto(User user);

    // Map UserDto -> User
    // Ignore password (handled separately) and any other missing fields like active
    @Mapping(target = "password", ignore = true)
    @Mapping(target = "roles", source = "roles")
    User toEntity(UserDto dto);
}
