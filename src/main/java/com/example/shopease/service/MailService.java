package com.example.shopease.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class MailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendResetPasswordEmail(String toEmail, String resetLink) {

        System.out.println(" Sending email to: " + toEmail);
        System.out.println(" Reset link: " + resetLink);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("ShopEase - Reset Your Password");
        message.setText(
                "Hello,\n\n" +
                "Click the link below to reset your password:\n\n" +
                resetLink +
                "\n\nThis link will expire in 30 minutes.\n\n" +
                "– ShopEase Team"
        );

        mailSender.send(message);

        System.out.println(" Email sent successfully");
    }
}
