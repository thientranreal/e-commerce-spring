package com.nashtech.ecommercespring.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String email;
    private String password;
    private String firstName;
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;

    public enum Role {
        CUSTOMER, ADMIN
    }

    @OneToMany(mappedBy = "user")
    private List<Rating> ratings;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user")
    private List<Order> orders;

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
