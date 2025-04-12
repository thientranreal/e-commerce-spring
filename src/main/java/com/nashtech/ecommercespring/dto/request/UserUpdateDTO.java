package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
public class UserUpdateDTO {
    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 60)
    private String password;

    @NotEmpty
    private Set<UUID> roleIds;
}
