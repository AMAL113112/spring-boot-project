package com.example.shopease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopease.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

    // LOGIN / AUTH
    Optional<User> findByUsernameIgnoreCase(String username);

    // FORGOT PASSWORD (THIS FIXES THE RED ERROR)
    Optional<User> findByEmailIgnoreCase(String email);
}
