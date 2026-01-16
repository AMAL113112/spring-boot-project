package com.example.shopease.controller;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shopease.entity.PasswordResetToken;
import com.example.shopease.entity.User;
import com.example.shopease.repository.PasswordResetTokenRepository;
import com.example.shopease.repository.UserRepository;
import com.example.shopease.service.MailService;

@Controller
public class ForgotPasswordController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordResetTokenRepository tokenRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private MailService mailService;

    // =========================
    // SHOW FORGOT PASSWORD PAGE
    // =========================
    @GetMapping("/forgot-password")
    public String forgotPasswordPage() {
        return "forgot-password";
    }

    // =========================
    // SEND RESET LINK
    // =========================
    @PostMapping("/forgot-password")
    @Transactional
    public String sendResetLink(
            @RequestParam String email,
            Model model
    ) {

        User user = userRepository
                .findByEmailIgnoreCase(email)
                .orElse(null);

        if (user == null) {
            model.addAttribute("error", "Email not found");
            return "forgot-password";
        }

        String token = UUID.randomUUID().toString();

        //  UPDATE EXISTING TOKEN OR CREATE NEW
        PasswordResetToken resetToken = tokenRepository
                .findByUser(user)
                .orElse(new PasswordResetToken());

        resetToken.setUser(user);
        resetToken.setToken(token);
        resetToken.setExpiryDate(LocalDateTime.now().plusMinutes(30));

        tokenRepository.save(resetToken);

        String resetLink =
                "http://localhost:8080/reset-password?token=" + token;

        mailService.sendResetPasswordEmail(
                user.getEmail(),
                resetLink
        );

        model.addAttribute(
                "message",
                "Reset link has been sent to your email"
        );

        return "forgot-password";
    }

    // =========================
    // SHOW RESET PASSWORD PAGE
    // =========================
    @GetMapping("/reset-password")
    public String resetPasswordPage(
            @RequestParam String token,
            Model model
    ) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token).orElse(null);

        if (resetToken == null ||
            resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid or expired reset link");
            return "forgot-password";
        }

        model.addAttribute("token", token);
        return "reset-password";
    }

    // =========================
    // SAVE NEW PASSWORD
    // =========================
    @PostMapping("/reset-password")
    @Transactional
    public String resetPassword(
            @RequestParam String token,
            @RequestParam String password,
            Model model
    ) {

        PasswordResetToken resetToken =
                tokenRepository.findByToken(token).orElse(null);

        if (resetToken == null ||
            resetToken.getExpiryDate().isBefore(LocalDateTime.now())) {

            model.addAttribute("error", "Invalid or expired reset link");
            return "forgot-password";
        }

        User user = resetToken.getUser();

        //  BCrypt encode (fixes login mismatch)
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        //  Invalidate token after use
        tokenRepository.delete(resetToken);

        model.addAttribute(
                "success",
                "Password reset successful. Please login."
        );

        return "login";
    }
}
