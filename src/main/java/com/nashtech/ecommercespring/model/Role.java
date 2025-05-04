package com.nashtech.ecommercespring.model;

import com.nashtech.ecommercespring.enums.RoleName;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Entity
@Getter
@Setter
@Table(name = "roles")
public class Role {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(length = 20, nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;
}
