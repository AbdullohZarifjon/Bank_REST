package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Builder;

@Builder
public record UpdateUserRequestDto(

        @NotBlank(message = "First name must not be empty")
        @Size(max = 50, message = "First name must not exceed 50 characters")
        String firstName,

        @NotBlank(message = "Last name must not be empty")
        @Size(max = 50, message = "Last name must not exceed 50 characters")
        String lastName,

        @NotBlank(message = "Username must not be empty")
        @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
        String username,

        @NotBlank(message = "Phone number must not be empty")
        @Pattern(
                regexp = "^\\+?[0-9]{9,15}$",
                message = "Phone number must be valid and contain between 9 and 15 digits"
        )
        String phoneNumber,

        @NotBlank(message = "Old password must not be empty")
        @Size(min = 8, message = "Old password must be at least 8 characters long")
        String oldPassword,

        @NotBlank(message = "New password must not be empty")
        @Size(min = 8, message = "New password must be at least 8 characters long")
        String newPassword
) {
}
