package com.example.ecommerce.controller;


import com.example.ecommerce.dto.ApiResponse;
import com.example.ecommerce.dto.ChangePasswordDto;
import com.example.ecommerce.dto.UpdateUserDto;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Optional;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;


    @GetMapping("/profile")
    public ResponseEntity<ApiResponse<UserDto>> getCurrentUserProfile() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401)
                    .body(ApiResponse.error("User is not authenticated"));
        }

        String username = authentication.getName();
        UserDto userDto = userService.getUserByUsername(username); // You need this method in UserService
        return ResponseEntity.ok(ApiResponse.success("Current user profile", userDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserDto>> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(
                ApiResponse.success("User fetched successfully", userService.getUserById(id))
        );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<?>> getAllUsers(
            @RequestParam("search") Optional<String> search,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "updatedAt") String sortBy,
            @RequestParam(defaultValue = "false") boolean ascending
    ) {
        Sort sort = ascending ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<UserDto> users;
        String query = search.orElse(null);

        if (search.isPresent() && !query.isBlank()) {
            users = userService.searchUsers(query, pageable);
        } else {
            users = userService.getAllUsers(pageable);
        }

        return ResponseEntity.ok(
                ApiResponse.success("Users retrieved successfully", users)
        );
    }


    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UpdateUserDto>> updateUser(@PathVariable Long id, @RequestBody UpdateUserDto dto) {
        return ResponseEntity.ok(
                ApiResponse.success("User updated successfully", userService.updateUser(id, dto))
        );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully", null));
    }

    @PutMapping(value = "/{id}/upload-pp", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<UserDto>> uploadProfilePicture(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file) throws IOException {
        UserDto updatedUser = userService.uploadProfilePicture(id, file);
        return ResponseEntity.ok(ApiResponse.success("Profile picture uploaded successfully", updatedUser));
    }
    @PutMapping("/{userId}/change-password")
    @PreAuthorize("hasRole('ADMIN') or #userId == principal.id")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long userId,
            @RequestBody ChangePasswordDto request
    ) {
        try {
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(ApiResponse.success("Password changed successfully", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Something went wrong: " + ex.getMessage()));
        }
    }

    @PutMapping("/{userId}/toggle-status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<Void>> changeUserStatus(@PathVariable Long userId) {
        try {
            userService.changeUserStatus(userId);
            return ResponseEntity.ok(ApiResponse.success("User status updated successfully", null));
        } catch (RuntimeException ex) {
            return ResponseEntity.badRequest().body(ApiResponse.error(ex.getMessage()));
        } catch (Exception ex) {
            return ResponseEntity.internalServerError()
                    .body(ApiResponse.error("Something went wrong: " + ex.getMessage()));
        }
    }

}
