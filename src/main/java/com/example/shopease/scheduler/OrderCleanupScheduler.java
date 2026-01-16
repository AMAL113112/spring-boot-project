package com.example.shopease.scheduler;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.example.shopease.entity.Order;
import com.example.shopease.repository.OrderRepository;

@Component
public class OrderCleanupScheduler {

    @Autowired
    private OrderRepository orderRepository;

    // ======================================
    // AUTO DELETE CANCELLED & DELIVERED ORDERS
    // Runs once every day at 2 AM
    // ======================================
    @Scheduled(cron = "0 0 2 * * ?")
    public void deleteOldOrders() {

        // 30 days ago
        LocalDateTime cutoffDate = LocalDateTime.now().minusDays(30);

        // Statuses eligible for deletion
        List<String> statuses = List.of("CANCELLED", "DELIVERED");

        // Fetch old orders
        List<Order> oldOrders =
                orderRepository.findByStatusInAndCreatedAtBefore(
                        statuses,
                        cutoffDate
                );

        if (!oldOrders.isEmpty()) {
            orderRepository.deleteAll(oldOrders);
            System.out.println(
                "🧹 Deleted " + oldOrders.size() +
                " old CANCELLED/DELIVERED orders"
            );
        }
    }
}
