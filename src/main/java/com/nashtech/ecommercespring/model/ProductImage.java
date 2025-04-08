package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
