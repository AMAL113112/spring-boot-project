package com.example.shopease.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import com.example.shopease.entity.Order;
import com.example.shopease.entity.OrderItem;
import com.example.shopease.repository.OrderItemRepository;
import com.example.shopease.repository.OrderRepository;

@Controller
@RequestMapping("/admin")
public class AdminOrderController {

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private OrderItemRepository orderItemRepository;

    // =========================
    // ADMIN: VIEW ALL ORDERS
    // =========================
    @GetMapping("/orders")
    public String allOrders(Model model) {

        List<Order> orders = orderRepository.findAll(
                Sort.by(Sort.Direction.DESC, "createdAt")
        );

        model.addAttribute("orders", orders);
        return "admin-orders";
    }

    // =========================
    // ADMIN: VIEW ORDER DETAILS
    // =========================
    @GetMapping("/orders/{id}")
    public String orderDetailsAdmin(
            @PathVariable Long id,
            Model model
    ) {

        Order order = orderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // ✅ FETCH ONLY THIS ORDER'S ITEMS
        List<OrderItem> items = orderItemRepository.findByOrder(order);

        // ✅ CALCULATE TOTAL FOR THIS ORDER ONLY
        double displayTotal = items.stream()
                .mapToDouble(OrderItem::getTotalPrice)
                .sum();

        model.addAttribute("order", order);
        model.addAttribute("items", items);
        model.addAttribute("displayTotal", displayTotal);

        return "admin-order-details";
    }

    // =========================
    // ADMIN: DELETE ORDER
    // =========================
    @PostMapping("/orders/delete")
    public String deleteOrder(@RequestParam Long orderId) {
        orderRepository.deleteById(orderId);
        return "redirect:/admin/orders";
    }

    // =========================
    // ADMIN: UPDATE ORDER STATUS
    // =========================
    @PostMapping("/orders/update-status")
    public String updateOrderStatus(
            @RequestParam Long orderId,
            @RequestParam String status
    ) {

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!status.equals("PENDING")
                && !status.equals("PLACED")
                && !status.equals("DELIVERED")
                && !status.equals("CANCELLED")) {
            throw new RuntimeException("Invalid order status");
        }

        order.setStatus(status);
        orderRepository.save(order);

        return "redirect:/admin/orders";
    }
}
