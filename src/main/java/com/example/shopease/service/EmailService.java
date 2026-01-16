package com.example.shopease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ShopEase - Reset Your Password");
        message.setText(
                "Click the link below to reset your password:\n\n" +
                resetLink +
                "\n\nThis link is valid for 30 minutes."
        );

        mailSender.send(message);
    }
}
