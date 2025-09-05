package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.*;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record AddCardRequestDto(

        @NotBlank(message = "Card name must not be empty")
        @Size(max = 50, message = "Card name must not exceed 50 characters")
        String name,

        @NotBlank(message = "Card number must not be empty")
        @Pattern(
                regexp = "^[0-9]{16}$",
                message = "Card number must be exactly 16 digits"
        )
        String number,

        @NotNull(message = "Expiry date must not be null")
        @Future(message = "Expiry date must be in the future")
        LocalDate expiryDate,

        @NotNull(message = "Balance must not be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Balance cannot be negative")
        @Digits(integer = 15, fraction = 2, message = "Balance must be a valid monetary amount")
        BigDecimal balance
) {
}
