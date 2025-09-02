package com.example.ecommerce.controller;


import com.example.ecommerce.entity.dto.ApiResponse;
import com.example.ecommerce.entity.dto.AuthRequest;
import com.example.ecommerce.entity.dto.AuthResponse;
import com.example.ecommerce.entity.dto.UserDto;
import com.example.ecommerce.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final UserService userService;

    @PostMapping
    public ResponseEntity<ApiResponse<UserDto>> createUser(@RequestBody UserDto userDto) {
        // Extract password from request attributes if available
        String password = userDto.getPassword();
        
        // Create the user using the service
        UserDto createdUser = userService.createUser(userDto, password);
        
        // Return success response with created user data
        return ResponseEntity.ok(ApiResponse.success("User created successfully", createdUser));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> loginUser(@Valid @RequestBody AuthRequest request) {
        log.info("Login request received for: {}", request.getUsernameOrEmail());
        AuthResponse response = userService.loginUser(request);
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

}