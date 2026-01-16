package com.example.shopease.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.example.shopease.entity.Product;
import com.example.shopease.repository.ProductRepository;

@Controller
public class HomeController {

    @Autowired
    private ProductRepository productRepository;

    @GetMapping("/")
    public String home(Model model) {

        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);

        return "index";
    }
    
    // =========================
    // SEARCH RESULTS PAGE
    // =========================
    @GetMapping("/search")
    public String searchProducts(
            @RequestParam String keyword,
            Model model
    ) {
        String normalizedKeyword = keyword.toLowerCase().trim();

        // Remove plural 's'
        if (normalizedKeyword.endsWith("s")) {
            normalizedKeyword = normalizedKeyword.substring(0, normalizedKeyword.length() - 1);
        }

        List<Product> products =
                productRepository.findByNameContainingIgnoreCase(normalizedKeyword);

        model.addAttribute("products", products);
        model.addAttribute("keyword", keyword);

        return "search-results";
    }

}
