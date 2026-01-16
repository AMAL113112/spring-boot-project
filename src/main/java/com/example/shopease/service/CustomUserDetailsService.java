package com.example.shopease.service;

import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.shopease.entity.User;
import com.example.shopease.repository.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username)
            throws UsernameNotFoundException {

        System.out.println("===== LOGIN ATTEMPT =====");
        System.out.println("USERNAME ENTERED: " + username);

        User user = userRepository.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        System.out.println(" USER FOUND");
        System.out.println("DB USERNAME: " + user.getUsername());
        System.out.println("DB ROLE: " + user.getRole());
        System.out.println("DB PASSWORD HASH: " + user.getPassword());

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),

                //  THIS LINE IS THE FIX
                Collections.singletonList(
                        new SimpleGrantedAuthority(user.getRole())
                )
        );
    }
}
