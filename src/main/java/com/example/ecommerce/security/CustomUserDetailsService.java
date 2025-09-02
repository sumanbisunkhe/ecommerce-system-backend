package com.example.ecommerce.security;

import com.example.ecommerce.entity.User;
import com.example.ecommerce.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetailsService.class);

    @Autowired
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        // Lookup by username or email
        User user = userRepository.findByUsername(identifier)
                .or(() -> userRepository.findByEmail(identifier))
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not found with identifier: " + identifier
                ));

        // Remove the isActive check to allow login regardless of active status
        // The active status will be set to true after successful authentication

        // Map roles to authorities
        var authorities = user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.name()))
                .toList();

        logger.debug("Loading user with roles: " + authorities);

        return new CustomUserDetails(
                user.getUsername(),
                user.getPassword(),
                true, // Always enable the user for authentication
                true,
                true,
                true,
                authorities,
                user.getId()
        );
    }
}
