package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record LoginRequestDto(
        @NotBlank(message = "Login cannot be blank")
        String login,

        @NotBlank(message = "Password cannot be blank")
        String password
) {
}
