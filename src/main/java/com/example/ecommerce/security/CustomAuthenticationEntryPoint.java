package com.example.ecommerce.security;

import com.example.ecommerce.entity.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void commence(HttpServletRequest request,
                         HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "UNAUTHORIZED",
                authException.getMessage() != null ? authException.getMessage() : "Authentication required"
        );

        ApiResponse<Object> apiResponse = ApiResponse.error("Unauthorized", errorDetails);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
