package com.example.bank_rest.payload.dto.response;

import com.example.bank_rest.entity.enums.StatusCard;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;

@Builder
public record CardResponseDto(
        Long id,
        String name,
        String number,
        LocalDate expiryDate,
        StatusCard status,
        BigDecimal balance) {
}
