package com.example.shopease.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.shopease.entity.Order;
import com.example.shopease.entity.User;

public interface OrderRepository extends JpaRepository<Order, Long> {

    // =========================
    // USER: ALL ORDERS (OLD – still valid)
    // =========================
    List<Order> findByUser(User user);

    // =========================
    // USER: HIDE CANCELLED & DELIVERED (NEW)
    // =========================
    List<Order> findByUserAndStatusNotInOrderByCreatedAtDesc(
            User user,
            List<String> statuses
    );

    // =========================
    // ADMIN: ALL ORDERS (LATEST FIRST)
    // =========================
    List<Order> findAllByOrderByCreatedAtDesc();

    // =========================
    // AUTO DELETE (AFTER 30 DAYS)
    // =========================
    List<Order> findByStatusInAndCreatedAtBefore(
            List<String> statuses,
            LocalDateTime dateTime
    );
}
