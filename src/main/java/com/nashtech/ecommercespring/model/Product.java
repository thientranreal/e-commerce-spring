package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String name;
    private String description;
    private BigDecimal price;
    private boolean isFeatured;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product")
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product")
    private List<Rating> ratings;

    @OneToMany(mappedBy = "product")
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product")
    private List<OrderItem> orderItems;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
        this.lastUpdatedOn = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.lastUpdatedOn = LocalDateTime.now();
    }
}
