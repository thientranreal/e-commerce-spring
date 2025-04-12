package com.nashtech.ecommercespring.dto.request;

import com.nashtech.ecommercespring.enums.RoleName;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserCreateDTO {
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

    private RoleName roleName;
}
