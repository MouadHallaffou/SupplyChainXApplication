package com.supplychainx.service_user.dto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class UserRequestDTO {
    private Long userId;

    @Size(max = 50, message = "First name must be less than 50 characters")
    @NotBlank(message = "First name is required")
    private String firstName;

    @Size(max = 50, message = "Last name must be less than 50 characters")
    @NotBlank(message = "Last name is required")
    private String lastName;

    @Size(max = 100, message = "Email must be less than 100 characters")
    @NotBlank(message = "Email is required")
    @Email(message = "Email should be valid")
    private String email;

    @Size(min = 6, max = 100, message = "PasswordUtil must be between 6 and 100 characters")
    @NotBlank(message = "PasswordUtil is required")
    private String password;

//    @NotNull(message = "Active status is required")
    private Boolean isActive;

    @NotNull(message = "Role ID is required")
    private Long roleId;
}
