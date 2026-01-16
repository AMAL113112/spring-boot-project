package com.example.shopease.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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
public class PaymentController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private OrderRepository orderRepository;

    // =========================
    // SHOW PAYMENT PAGE
    // =========================
    @GetMapping("/payment")
    public String paymentPage(
            @RequestParam(required = false) Long productId,
            @RequestParam(defaultValue = "1") int quantity,
            Model model
    ) {

        if (productId == null) {
            return "redirect:/";
        }

        if (quantity <= 0) {
            quantity = 1;
        }

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        double total = product.getPrice() * quantity;

        model.addAttribute("product", product);
        model.addAttribute("quantity", quantity);
        model.addAttribute("total", total);

        return "payment";
    }

    // =========================
    // PAYMENT SUCCESS
    // =========================
    @PostMapping("/payment/success")
    @Transactional
    public String paymentSuccess(
            Principal principal,

            @RequestParam Long productId,
            @RequestParam int quantity,

            @RequestParam String customerName,
            @RequestParam String phone,
            @RequestParam String address,
            @RequestParam String paymentMethod
    ) {

        if (quantity <= 0) {
            quantity = 1;
        }

        User user = userRepository
                .findByUsernameIgnoreCase(principal.getName())
                .orElseThrow(() -> new RuntimeException("User not found"));

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        if (quantity > product.getQuantity()) {
            throw new RuntimeException("Not enough stock");
        }

        // Reduce stock
        product.setQuantity(product.getQuantity() - quantity);
        productRepository.save(product);

        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setStatus("PLACED");
        order.setCustomerName(customerName);
        order.setPhone(phone);
        order.setAddress(address);
        order.setPaymentMethod(paymentMethod);

        // Create order item
        OrderItem orderItem = new OrderItem();
        orderItem.setOrder(order);
        orderItem.setProduct(product);
        orderItem.setQuantity(quantity);
        orderItem.setPrice(product.getPrice());
        orderItem.setStatus("ORDERED");

        List<OrderItem> items = new ArrayList<>();
        items.add(orderItem);

        order.setItems(items);
        order.setTotalAmount(product.getPrice() * quantity);

        orderRepository.save(order);

        return "redirect:/orders";
    }

    // =========================
    // PAYMENT CANCEL
    // =========================
    @PostMapping("/payment/cancel")
    public String paymentCancel() {
        return "redirect:/";
    }
}
