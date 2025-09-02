package com.example.ecommerce.service.impl;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.dto.AuthRequest;
import com.example.ecommerce.dto.AuthResponse;
import com.example.ecommerce.dto.UserDto;
import com.example.ecommerce.mapper.UserMapper;
import com.example.ecommerce.exceptions.ResourceNotFoundException;
import com.example.ecommerce.repository.UserRepository;
import com.example.ecommerce.security.CustomUserDetails;
import com.example.ecommerce.security.JwtUtil;
import com.example.ecommerce.service.FileUploadService;
import com.example.ecommerce.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final FileUploadService fileUploadService;

    @Override
    public UserDto createUser(UserDto dto, String rawPassword) {
        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new RuntimeException("Email already exists");
        }
        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new RuntimeException("Username already exists");
        }

        User user = userMapper.toEntity(dto);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());

        User saved = userRepository.save(user);
        return userMapper.toDto(saved);
    }

    @Override
    public AuthResponse loginUser(AuthRequest request) {

        // Authenticate user
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getUsernameOrEmail(), request.getPassword()
                )
        );

        // Find user by username or email
        User user = userRepository.findByUsernameOrEmail(request.getUsernameOrEmail())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "User", "username/email", request.getUsernameOrEmail()
                ));

        user.setActive(true);
        userRepository.save(user);

        // Generate JWT token
        String token = jwtUtil.generateToken(new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                user.getRoles().stream()
                        .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                        .toList(),
                user.getId()
        ));

        return AuthResponse.builder()
                .token(token)
                .expiresIn(jwtUtil.getExpirationTime())
                .user(userMapper.toDto(user))
                .build();
    }



    @Override
    public UserDto getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toDto)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
    }

    @Override
    public Page<UserDto> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(userMapper::toDto);
    }

    @Override
    public Page<UserDto> searchUsers(String keyword, Pageable pageable) {
        return userRepository
                .searchUsers(keyword, pageable)
                .map(userMapper::toDto);
    }


    @Override
    public UserDto updateUser(Long id, UserDto dto) {
        User existing = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));

        // Update fields
        existing.setFirstName(dto.getFirstName());
        existing.setMiddleName(dto.getMiddleName());
        existing.setLastName(dto.getLastName());
        existing.setGender(dto.getGender());
        existing.setAddress(dto.getAddress());
        existing.setCountry(dto.getCountry());
        existing.setProfilePictureUrl(dto.getProfilePictureUrl());
        existing.setRoles(dto.getRoles());
        existing.setUpdatedAt(LocalDateTime.now());

        return userMapper.toDto(userRepository.save(existing));
    }

    @Override
    public UserDto uploadProfilePicture(Long userId, MultipartFile file) throws IOException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "ID", userId));


        // Validate file
        if (!fileUploadService.isValidImage(file)) {
            throw new IllegalArgumentException("Invalid image file. Please upload a valid image (JPEG, PNG, GIF, WebP) under 10MB.");
        }

        // Upload to Cloudinary
        Map<String, String> uploadResult = fileUploadService.uploadImage(file, "ecommerce");

        // Update user with new profile picture
        user.setProfilePictureUrl((String) uploadResult.get("secure_url"));
        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }


    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }

    @Override
    public UserDto getUserByUsername(String username) {
        return userRepository.getUserByUsername(username)
                .map(userMapper::toDto)
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
    }

}
