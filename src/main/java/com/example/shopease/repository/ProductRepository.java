package com.example.shopease.repository;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.example.shopease.entity.Product;

public interface ProductRepository extends JpaRepository<Product, Long> {

    //  Basic search by product name (case-insensitive)
    List<Product> findByNameContainingIgnoreCase(String keyword);
}
