package com.nashtech.ecommercespring.dto;

import com.nashtech.ecommercespring.enums.Role;
import com.nashtech.ecommercespring.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
public class UserDTO {
    private UUID id;

    @Email
    private String email;

    @NotBlank
    @Size(min = 5, max = 60)
    private String password;

    @NotBlank
    @Size(max = 50)
    private String firstName;

    @NotBlank
    @Size(max = 50)
    private String lastName;

    @Size(max = 15)
    private String phone;

    @Size(max = 255)
    private String address;

    private Role role;

    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
        this.role = user.getRole();
    }
}
