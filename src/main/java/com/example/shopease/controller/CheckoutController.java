package com.example.shopease.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shopease.entity.Order;
import com.example.shopease.entity.OrderItem;
import com.example.shopease.entity.Product;
import com.example.shopease.entity.User;
import com.example.shopease.repository.OrderRepository;
import com.example.shopease.repository.ProductRepository;
import com.example.shopease.repository.UserRepository;

@Controller
public class CheckoutController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =========================
    // PLACE ORDER (BUY NOW)
    // =========================
    @PostMapping("/place-order")
    public String placeOrder(
            Principal principal,

            // PRODUCT SELECTION
            @RequestParam Long productId,
            @RequestParam int quantity,

            // DELIVERY DETAILS
            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String paymentMethod
    ) {

        // 1️ Logged-in user
        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️ Product user explicitly chose
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // 3️ Create Order (snapshot)
        Order order = new Order();
        order.setUser(user);
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);

        // 4️ Create OrderItem (ONLY THIS PRODUCT)
        OrderItem item = new OrderItem();
        item.setOrder(order);
        item.setProduct(product);
        item.setQuantity(quantity);
        item.setPrice(product.getPrice());
        item.setStatus("ORDERED");

        // 5️ Attach item to order
        order.setItems(List.of(item));

        // 6️ Calculate total ONLY from this item
        double total = product.getPrice() * quantity;
        order.setTotalAmount(total);

        // 7️ Save once (CASCADE saves item)
        orderRepository.save(order);

        return "redirect:/orders";
    }
}
