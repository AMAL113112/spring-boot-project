package com.example.shopease.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.shopease.entity.Order;
import com.example.shopease.entity.User;
import com.example.shopease.repository.OrderRepository;
import com.example.shopease.repository.UserRepository;

@Controller
public class OrderController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =========================
    // USER: VIEW ALL ORDERS
    // =========================
    @GetMapping("/orders")
    public String myOrders(Principal principal, Model model) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUser(user);

        model.addAttribute("orders", orders);
        return "orders";
    }

    // =========================
    // USER: ORDER DETAILS
    // =========================
    @GetMapping("/orders/{id}")
    public String orderDetails(
            @PathVariable Long id,
            Principal principal,
            Model model,
            @RequestParam(required = false) String cancelled
    ) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security check
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        model.addAttribute("order", order);
        model.addAttribute("items", order.getItems());

        // Success message after cancellation
        if (cancelled != null) {
            model.addAttribute("message", "Order cancelled successfully");
        }

        return "order-details";
    }

    // =========================
    // USER: CANCEL ORDER
    // =========================
    @PostMapping("/orders/cancel")
    public String cancelOrder(
            @RequestParam Long orderId,
            Principal principal
    ) {

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Security check
        if (!order.getUser().getId().equals(user.getId())) {
            throw new RuntimeException("Unauthorized access");
        }

        // Allow cancel only if not delivered
        if (!order.getStatus().equals("PENDING")
                && !order.getStatus().equals("PLACED")) {
            throw new RuntimeException("Order cannot be cancelled");
        }

        order.setStatus("CANCELLED");
        orderRepository.save(order);

        return "redirect:/orders/" + orderId + "?cancelled=true";
    }
}
