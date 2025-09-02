package com.example.ecommerce.security;

import com.example.ecommerce.entity.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public CustomAccessDeniedHandler(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void handle(HttpServletRequest request,
                       HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);

        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "ACCESS_DENIED",
                accessDeniedException.getMessage() != null ? accessDeniedException.getMessage()
                        : "You don't have permission to access this resource"
        );

        ApiResponse<Object> apiResponse = ApiResponse.error("Access denied", errorDetails);

        objectMapper.writeValue(response.getOutputStream(), apiResponse);
    }
}
