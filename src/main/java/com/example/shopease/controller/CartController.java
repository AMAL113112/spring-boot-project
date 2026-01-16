package com.example.shopease.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.shopease.entity.Cart;
import com.example.shopease.entity.CartItem;
import com.example.shopease.entity.Product;
import com.example.shopease.entity.User;
import com.example.shopease.repository.CartItemRepository;
import com.example.shopease.repository.CartRepository;
import com.example.shopease.repository.ProductRepository;
import com.example.shopease.repository.UserRepository;

@Controller
public class CartController {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private CartItemRepository cartItemRepository;

    // =========================
    // ADD TO CART (DEFAULT QTY = 1)
    // =========================
    @GetMapping("/cart/add/{id}")
    public String addToCart(
            @PathVariable Long id,
            @RequestParam(defaultValue = "1") int qty,
            Principal principal
    ) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (qty < 1) qty = 1;
        if (qty > product.getQuantity()) qty = product.getQuantity();

        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        CartItem item = cartItemRepository
                .findByCartAndProduct(cart, product)
                .orElse(null);

        if (item != null) {
            item.setQuantity(item.getQuantity() + qty);
        } else {
            item = new CartItem();
            item.setCart(cart);
            item.setProduct(product);
            item.setQuantity(qty);
        }

        cartItemRepository.save(item);
        return "redirect:/cart";
    }

    // =========================
    // BUY NOW (USER-SELECTED QTY)
    // =========================
    @GetMapping("/buy-now/{id}")
    public String buyNow(
            @PathVariable Long id,
            @RequestParam("qty") int qty,
            Principal principal
    ) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (qty < 1) qty = 1;
        if (qty > product.getQuantity()) qty = product.getQuantity();

        // 1️ Get or create cart
        Cart cart = cartRepository.findByUser(user)
                .orElseGet(() -> {
                    Cart newCart = new Cart();
                    newCart.setUser(user);
                    return cartRepository.save(newCart);
                });

        // 2️ CLEAR EXISTING CART ITEMS (VERY IMPORTANT)
        if (cart.getItems() != null && !cart.getItems().isEmpty()) {
            cartItemRepository.deleteAll(cart.getItems());
        }

        // 3️ ADD SELECTED PRODUCT WITH QTY
        CartItem item = new CartItem();
        item.setCart(cart);
        item.setProduct(product);
        item.setQuantity(qty);

        cartItemRepository.save(item);

        // 4️ REDIRECT TO PAYMENT
        return "redirect:/payment";
    }

    // =========================
    // VIEW CART
    // =========================
    @GetMapping("/cart")
    public String viewCart(Principal principal, Model model) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Cart cart = cartRepository.findByUser(user).orElse(null);

        List<CartItem> items = List.of();
        double total = 0;

        if (cart != null && cart.getItems() != null) {
            items = cart.getItems();
            total = items.stream()
                    .mapToDouble(CartItem::getTotalPrice)
                    .sum();
        }

        model.addAttribute("cartItems", items);
        model.addAttribute("total", total);

        return "cart";
    }

    // =========================
    // UPDATE CART ITEM
    // =========================
    @PostMapping("/cart/update")
    public String updateCart(
            @RequestParam Long itemId,
            @RequestParam int quantity
    ) {

        CartItem item = cartItemRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item not found"));

        if (quantity <= 0) {
            cartItemRepository.delete(item);
        } else {
            item.setQuantity(quantity);
            cartItemRepository.save(item);
        }

        return "redirect:/cart";
    }

    // =========================
    // REMOVE CART ITEM
    // =========================
    @GetMapping("/cart/remove/{id}")
    public String removeItem(@PathVariable Long id) {

        cartItemRepository.deleteById(id);
        return "redirect:/cart";
    }
}
