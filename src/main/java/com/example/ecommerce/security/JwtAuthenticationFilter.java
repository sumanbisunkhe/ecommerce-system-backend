package com.example.ecommerce.security;

import com.example.ecommerce.dto.ApiResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;
    private final UserDetailsService userDetailsService;
    private final ObjectMapper objectMapper;

    // Skip filter for public endpoints
    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) throws ServletException {
        String path = request.getRequestURI();
        return path.startsWith("/auth")
                || path.startsWith("/public")
                || path.startsWith("/products/all")
                || path.startsWith("/files/upload")
                || path.startsWith("/swagger-ui")
                || path.startsWith("/v3/api-docs")
                || path.startsWith("/ws");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain)
            throws ServletException, IOException {

        try {
            final String authHeader = request.getHeader("Authorization");

            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                // No token provided → skip authentication for non-public endpoints
                filterChain.doFilter(request, response);
                return;
            }

            String jwt = authHeader.substring(7);
            String username;

            try {
                username = jwtUtil.extractUsername(jwt);
            } catch (ExpiredJwtException e) {
                sendErrorResponse(response, "JWT token has expired", "TOKEN_EXPIRED");
                return;
            } catch (JwtException e) {
                sendErrorResponse(response, "Invalid JWT token", "INVALID_TOKEN");
                return;
            }

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtUtil.isTokenValid(jwt, userDetails)) {
                    UsernamePasswordAuthenticationToken authToken =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities()
                            );
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                } else {
                    sendErrorResponse(response, "Invalid or expired JWT token", "INVALID_TOKEN");
                    return;
                }
            }

            filterChain.doFilter(request, response);

        } catch (Exception e) {
            sendErrorResponse(response, "Authentication error: " + e.getMessage(), "AUTH_ERROR");
        }
    }

    private void sendErrorResponse(HttpServletResponse response, String errorMessage, String errorCode) throws IOException {
        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

        ApiResponse.ErrorDetails errorDetails = ApiResponse.ErrorDetails.builder()
                .errorCode(errorCode)
                .errorMessage(errorMessage)
                .build();

        String jsonResponse = objectMapper.writeValueAsString(
                ApiResponse.error("Authentication failed", errorDetails)
        );
        response.getWriter().write(jsonResponse);
    }
}
