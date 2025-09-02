package com.example.ecommerce.repository;

import com.example.ecommerce.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByEmail(String email);

    Optional<User> findByUsername(String username);

    boolean existsByEmail(String email);

    boolean existsByUsername(String username);

    @Query("""
       SELECT u FROM User u
       WHERE (:search IS NULL OR LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.firstName) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.middleName) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.lastName) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(STR(u.gender)) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.address) LIKE LOWER(CONCAT('%', :search, '%')))
          OR (:search IS NULL OR LOWER(u.country) LIKE LOWER(CONCAT('%', :search, '%')))
       """)
    Page<User> searchUsers(@Param("search") String search, Pageable pageable);

    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);

    @Query("SELECT u FROM User u WHERE u.username = :username")
    Optional<User> getUserByUsername(@Param("username") String username);
}
