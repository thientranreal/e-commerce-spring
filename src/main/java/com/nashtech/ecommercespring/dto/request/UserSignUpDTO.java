package com.nashtech.ecommercespring.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserSignUpDTO {

    @NotBlank
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

    @Pattern(regexp = "^\\+?[0-9]{10,15}$", message = "Invalid phone number")
    @Size(max = 15)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;
}
