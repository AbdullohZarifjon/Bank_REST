package com.example.bank_rest.payload.dto.response;

import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Builder
public record TransactionResponseDto(
        Long id,
        String fromCard,
        String toCard,
        BigDecimal amount,
        String status,
        LocalDateTime createdAt
) {
}
