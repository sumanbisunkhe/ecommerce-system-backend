package com.example.ecommerce.exceptions;

import com.example.ecommerce.entity.dto.ApiResponse;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.sql.SQLException;
import java.util.stream.Collectors;

@ControllerAdvice
public class GlobalExceptionHandler {

    // Handle generic runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiResponse<?>> handleRuntimeException(RuntimeException ex) {
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "RUNTIME_ERROR", ex.getMessage()
        );
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), errorDetails), HttpStatus.BAD_REQUEST);
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<?>> handleValidationException(MethodArgumentNotValidException ex) {
        String details = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "VALIDATION_FAILED", details
        );
        return new ResponseEntity<>(ApiResponse.error(details, errorDetails), HttpStatus.BAD_REQUEST);
    }

    // Handle database constraint violations
    @ExceptionHandler({DataIntegrityViolationException.class, ConstraintViolationException.class})
    public ResponseEntity<ApiResponse<?>> handleDataIntegrityViolation(Exception ex) {
        String message = "Database constraint violation: ";
        Throwable rootCause = getRootCause(ex);

        if (rootCause instanceof SQLException sqlEx) {
            message = formatSqlErrorMessage(sqlEx);
        } else {
            message += ex.getMessage();
        }

        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "DATA_INTEGRITY_VIOLATION", message
        );
        return new ResponseEntity<>(ApiResponse.error(message, errorDetails), HttpStatus.BAD_REQUEST);
    }

    private String formatSqlErrorMessage(SQLException sqlEx) {
        String message = sqlEx.getMessage();

        if (message.contains("violates not-null constraint")) {
            String column = extractColumnName(message);
            return "Required field '" + column + "' cannot be empty";
        } else if (message.contains("unique constraint") || message.contains("duplicate key")) {
            String column = extractColumnName(message);
            return "The " + column + " already exists. Please use a different value.";
        }

        return "Database error: " + message;
    }

    private String extractColumnName(String errorMessage) {
        if (errorMessage.contains("column")) {
            int startIndex = errorMessage.indexOf("column \"") + 8;
            int endIndex = errorMessage.indexOf("\"", startIndex);
            if (startIndex > 0 && endIndex > startIndex) {
                return errorMessage.substring(startIndex, endIndex);
            }
        }
        return "unknown field";
    }

    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return (cause != null) ? getRootCause(cause) : throwable;
    }

    // Handle access denied exceptions
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<?>> handleAccessDeniedException(AccessDeniedException ex) {
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "ACCESS_DENIED", "You don't have permission to access this resource"
        );
        return new ResponseEntity<>(ApiResponse.error("Access denied", errorDetails), HttpStatus.FORBIDDEN);
    }
    
    // Handle JWT authentication errors
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<?>> handleBadCredentialsException(BadCredentialsException ex) {
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "INVALID_TOKEN", "Missing or invalid Authorization header"
        );
        return new ResponseEntity<>(ApiResponse.error("Authentication failed", errorDetails), HttpStatus.UNAUTHORIZED);
    }

    // Handle all uncaught exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<?>> handleException(Exception ex) {
        ApiResponse.ErrorDetails errorDetails = new ApiResponse.ErrorDetails(
                "INTERNAL_SERVER_ERROR", ex.getMessage()
        );
        return new ResponseEntity<>(ApiResponse.error("Internal Server Error", errorDetails),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
