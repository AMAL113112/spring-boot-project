package com.example.shopease.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import com.example.shopease.service.CartService;

@ControllerAdvice
public class GlobalModelConfig {

    private final CartService cartService;

    public GlobalModelConfig(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cartCount")
    public int cartCount() {
        return cartService.getTotalItems();
    }
}
