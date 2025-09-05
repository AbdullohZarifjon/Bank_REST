package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class RefreshTokenDto {

    @NotBlank(message = "Refresh token cannot be empty.")
    private String refreshToken;
}
