package com.nashtech.ecommercespring.dto;

import com.nashtech.ecommercespring.model.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserSignUpDTO {

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

    @NotBlank
    @Size(max = 15)
    private String phone;

    @NotBlank
    @Size(max = 255)
    private String address;

    public UserSignUpDTO(User user) {
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.phone = user.getPhone();
        this.address = user.getAddress();
    }
}
