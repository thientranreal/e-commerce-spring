package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
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

    @Min(1)
    @Max(5)
    private int ratingValue;

    @Size(max = 1024)
    @Column(length = 1024)
    private String comment;

    private LocalDateTime createdOn;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @PrePersist
    protected void onCreate() {
        this.createdOn = LocalDateTime.now();
    }
}
