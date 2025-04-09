package com.nashtech.ecommercespring.model;

import com.nashtech.ecommercespring.enums.Role;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @Email
    @Column(unique = true)
    private String email;

    @Size(min = 5, max = 60)
    @Column(length = 60)
    private String password;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    @Column(length = 50)
    private String lastName;

    @Enumerated(EnumType.STRING)
    private Role role;

    private LocalDateTime createdOn;
    private LocalDateTime lastUpdatedOn;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    private List<Rating> ratings;

    @OneToOne(mappedBy = "user")
    private Cart cart;

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
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
