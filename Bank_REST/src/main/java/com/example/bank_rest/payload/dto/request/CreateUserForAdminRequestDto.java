package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.util.List;

@Builder
public record CreateUserForAdminRequestDto(

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

        @NotBlank(message = "Password must not be empty")
        @Size(min = 8, message = "Password must be at least 8 characters long")
        String password,

        @NotNull(message = "Roles must not be null")
        @Size(min = 1, message = "At least one role is required")
        List<
                @NotNull(message = "Role ID must not be null")
                @Positive(message = "Role ID must be a positive number")
                        Long> roleIds
) {
}
