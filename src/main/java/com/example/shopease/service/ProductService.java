package com.example.shopease.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.shopease.entity.Product;
import com.example.shopease.repository.ProductRepository;

@Service
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public List<Product> searchProducts(String keyword) {
        return productRepository.findByNameContainingIgnoreCase(keyword);
    }

    public void saveProduct(Product product) {
        productRepository.save(product);
    }
}
