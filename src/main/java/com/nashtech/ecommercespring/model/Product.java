package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank
    @Size(max = 100)
    @Column(length = 100)
    private String name;

    @Size(max = 2000)
    @Column(length = 2000)
    private String description;

    @DecimalMin(value = "0", inclusive = false)
    @Column(precision = 15)
    private BigDecimal price;

    private boolean isFeatured;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<ProductImage> productImages;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<Rating> ratings;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
    private List<CartItem> cartItems;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY)
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
