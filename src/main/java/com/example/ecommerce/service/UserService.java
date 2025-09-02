package com.example.ecommerce.service;

import com.example.ecommerce.entity.dto.AuthRequest;
import com.example.ecommerce.entity.dto.AuthResponse;
import com.example.ecommerce.entity.dto.UserDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;

public interface UserService {
    UserDto createUser(UserDto dto, String rawPassword);
    AuthResponse loginUser(AuthRequest request);
    UserDto getUserById(Long id);
    Page<UserDto> getAllUsers(Pageable pageable);
    Page<UserDto> searchUsers(String keyword, Pageable pageable);
    UserDto updateUser(Long id, UserDto dto);
    UserDto uploadProfilePicture(Long userId, org.springframework.web.multipart.MultipartFile file) throws java.io.IOException;

    void deleteUser(Long id);

    UserDto getUserByUsername(String username);
}
