package com.example.shopease.controller;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.example.shopease.entity.Product;
import com.example.shopease.repository.ProductRepository;

@Controller
@RequestMapping("/admin")
public class AdminProductController {

    //  Keep uploads OUTSIDE project folder (good practice)
    private static final Path UPLOAD_DIR =
            Paths.get("C:/Users/amall/OneDrive/Documents/springProject/ShopEase/uploads");

    @Autowired
    private ProductRepository productRepository;

    // =========================
    // SHOW ALL PRODUCTS
    // =========================
    @GetMapping("/products")
    public String showAdminProducts(Model model) {
        List<Product> products = productRepository.findAll();
        model.addAttribute("products", products);
        return "admin-products";
    }

    // =========================
    // SHOW ADD PRODUCT PAGE
    // =========================
    @GetMapping("/add-product")
    public String showAddProductForm(Model model) {
        model.addAttribute("product", new Product());
        return "add-product";
    }

    // =========================
    // SAVE NEW PRODUCT
    // =========================
    @PostMapping("/save-product")
    public String saveProduct(
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        if (image != null && !image.isEmpty()) {
            Files.createDirectories(UPLOAD_DIR);

            String fileName =
                    System.currentTimeMillis() + "_" + image.getOriginalFilename();

            Path filePath = UPLOAD_DIR.resolve(fileName);
            Files.write(filePath, image.getBytes());

            product.setImageName(fileName);
        }

        productRepository.save(product);
        return "redirect:/admin/products";
    }

    // =========================
    // SHOW EDIT PRODUCT PAGE
    // =========================
    @GetMapping("/edit-product")
    public String showEditProduct(
            @RequestParam Long id,
            Model model
    ) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        model.addAttribute("product", product);
        return "edit-product";
    }

    // =========================
    // UPDATE PRODUCT (WITH IMAGE REPLACE)
    // =========================
    @PostMapping("/update-product")
    public String updateProduct(
            @ModelAttribute Product product,
            @RequestParam("image") MultipartFile image
    ) throws IOException {

        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Update text fields
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());

        // If new image uploaded
        if (image != null && !image.isEmpty()) {

            // Delete old image safely
            if (existingProduct.getImageName() != null) {
                Path oldImagePath =
                        UPLOAD_DIR.resolve(existingProduct.getImageName());

                Files.deleteIfExists(oldImagePath);
            }

            Files.createDirectories(UPLOAD_DIR);

            String newFileName =
                    System.currentTimeMillis() + "_" + image.getOriginalFilename();

            Path newPath = UPLOAD_DIR.resolve(newFileName);
            Files.write(newPath, image.getBytes());

            existingProduct.setImageName(newFileName);
        }

        productRepository.save(existingProduct);
        return "redirect:/admin/products";
    }

    // =========================
    // DELETE PRODUCT (ADMIN)
    // =========================
    @PostMapping("/delete-product")
    public String deleteProduct(@RequestParam Long id) {

        Product product = productRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Delete image from disk
        if (product.getImageName() != null) {
            try {
                Path imagePath =
                        UPLOAD_DIR.resolve(product.getImageName());
                Files.deleteIfExists(imagePath);
            } catch (IOException e) {
                System.err.println("Failed to delete image: " + e.getMessage());
            }
        }

        productRepository.delete(product);
        return "redirect:/admin/products";
    }
}
