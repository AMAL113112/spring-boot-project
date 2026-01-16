package com.example.shopease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // =========================
    // ORDER (Parent)
    // =========================
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    // =========================
    // PRODUCT
    // =========================
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // =========================
    // ORDER DETAILS
    // =========================
    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false)
    private double price; // price at order time

    // =========================
    // ITEM STATUS
    // =========================
    // ORDERED, CANCELLED, DELIVERED
    @Column(nullable = false, length = 20)
    private String status;

    // =========================
    // AUTO DEFAULT STATUS
    // =========================
    @PrePersist
    public void setDefaultStatus() {
        if (this.status == null) {
            this.status = "ORDERED";
        }
    }

    // =========================
    // TOTAL (USED BY THYMELEAF)
    // =========================
    @Transient
    public double getTotalPrice() {
        return this.price * this.quantity;
    }

    // =========================
    // GETTERS & SETTERS
    // =========================
    public Long getId() {
        return id;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
