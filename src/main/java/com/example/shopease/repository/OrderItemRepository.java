package com.example.shopease.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.shopease.entity.Order;
import com.example.shopease.entity.OrderItem;

@Repository
public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    void deleteByProductId(Long productId);
    
    List<OrderItem> findByOrder(Order order);
    
}  
