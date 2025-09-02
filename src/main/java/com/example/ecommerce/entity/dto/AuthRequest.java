package com.example.ecommerce.entity.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    @NotBlank(message = "Username or email is required")
    private String usernameOrEmail;

    @NotBlank(message = "Password is required")
    private String password;
}
