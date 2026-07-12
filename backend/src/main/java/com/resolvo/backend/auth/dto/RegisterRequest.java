package com.resolvo.backend.auth.dto;

import com.resolvo.backend.common.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    @Size(max = 120, message = "Full name must be at most 120 characters")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 150, message = "Email must be at most 150 characters")
    private String email;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 100, message = "Password must be between 8 and 100 characters")
    private String password;

    @NotBlank(message = "Flat number is required")
    @Size(max = 20, message = "Flat number must be at most 20 characters")
    private String flatNumber;

    @Pattern(regexp = "^$|^[0-9+()\\-\\s]{7,20}$", message = "Phone number must be 7-20 digits, optionally with +, -, (), or spaces")
    private String phoneNumber;

    @NotNull(message = "Role is required")
    private UserRole role;
}