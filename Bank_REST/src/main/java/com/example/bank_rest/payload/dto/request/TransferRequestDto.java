package com.example.bank_rest.payload.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;

import java.math.BigDecimal;

@Builder
public record   TransferRequestDto(

        @NotNull(message = "Sender card id must not be null")
        Long fromCardId,

        @NotNull(message = "Receiver card id must not be null")
        Long toCardId,

        @NotNull(message = "Amount must not be null")
        @DecimalMin(value = "0.01", message = "Transfer amount must be greater than 0")
        BigDecimal amount
) {
}
