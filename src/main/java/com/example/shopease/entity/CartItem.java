package com.example.shopease.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "cart_items")
public class CartItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MANY ITEMS → ONE CART
    @ManyToOne
    @JoinColumn(name = "cart_id")
    private Cart cart;

    // MANY ITEMS → ONE PRODUCT
    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    private int quantity;

    public double getTotalPrice() {
        return product.getPrice() * quantity;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Cart getCart() {
		return cart;
	}

	public void setCart(Cart cart) {
		this.cart = cart;
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
    
    
    
}
