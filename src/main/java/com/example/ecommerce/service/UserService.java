package com.example.ecommerce.service;

import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.UpdateUserDto;
import com.example.ecommerce.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    UserDto createUser(UserDto dto, String rawPassword);

    AuthResponse loginUser(AuthRequest request);

    UserDto getUserById(Long id);

    Page<UserDto> getAllUsers(Pageable pageable);

    Page<UserDto> searchUsers(String keyword, Pageable pageable);

    UpdateUserDto updateUser(Long id, UpdateUserDto dto);

    UserDto uploadProfilePicture(Long userId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;

    void deleteUser(Long id);

    UserDto getUserByUsername(String username);
}
