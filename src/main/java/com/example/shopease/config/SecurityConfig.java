package com.example.shopease.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
            .authorizeHttpRequests(auth -> auth

                // =========================
                // PUBLIC (NO LOGIN REQUIRED)
                // =========================
                .requestMatchers(
                        "/",
                        "/login",
                        "/signup",

                        //  FORGOT / RESET PASSWORD
                        "/forgot-password",
                        "/reset-password",

                        // PRODUCT & STATIC
                        "/product/**",
                        "/uploads/**",
                        "/css/**",
                        "/js/**"
                ).permitAll()

                // =========================
                // USER (LOGIN REQUIRED)
                // =========================
                .requestMatchers(
                        "/cart/**",
                        "/orders/**",
                        "/buy-now/**",
                        "/payment/**"
                ).authenticated()

                // =========================
                // ADMIN ONLY
                // =========================
                .requestMatchers("/admin/**")
                .hasAuthority("ROLE_ADMIN")

                // =========================
                // EVERYTHING ELSE
                // =========================
                .anyRequest().authenticated()
            )

            // =========================
            // LOGIN CONFIG
            // =========================
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/", true)
                .permitAll()
            )

            // =========================
            // LOGOUT CONFIG
            // =========================
            .logout(logout -> logout
                .logoutUrl("/logout")
                .logoutSuccessUrl("/")
                .permitAll()
            );

        return http.build();
    }

    // =========================
    // PASSWORD ENCODER
    // =========================
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
