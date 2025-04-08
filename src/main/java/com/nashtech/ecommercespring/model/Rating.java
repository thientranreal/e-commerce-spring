package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ratings")
@Getter
@Setter
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private int ratingValue;
    private String comment;
    private LocalDateTime createdOn;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }
}
