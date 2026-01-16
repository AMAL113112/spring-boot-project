package com.example.shopease.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class CartService {

    // productId -> quantity
    private final Map<Long, Integer> cart = new HashMap<>();

    // ADD PRODUCT
    public void addToCart(Long productId) {
        cart.put(productId, cart.getOrDefault(productId, 0) + 1);
    }

    // TOTAL ITEMS COUNT
    public int getTotalItems() {
        return cart.values().stream().mapToInt(Integer::intValue).sum();
    }

    // GET CART (later use)
    public Map<Long, Integer> getCart() {
        return cart;
    }
}
