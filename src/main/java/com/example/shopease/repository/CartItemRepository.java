package com.example.shopease.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopease.entity.Cart;
import com.example.shopease.entity.CartItem;
import com.example.shopease.entity.Product;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    //  REQUIRED FOR addToCart logic
    Optional<CartItem> findByCartAndProduct(Cart cart, Product product);

    //  REQUIRED FOR admin delete product
    void deleteByProductId(Long productId);
}
