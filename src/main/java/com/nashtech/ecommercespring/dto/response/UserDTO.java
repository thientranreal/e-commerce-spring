package com.nashtech.ecommercespring.dto.response;

import com.nashtech.ecommercespring.model.Role;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserDTO {
    private UUID id;

    private String email;

    private LocalDateTime createdOn;

    private LocalDateTime lastUpdatedOn;

    private boolean isDeleted;

    private Set<Role> roles;
}
