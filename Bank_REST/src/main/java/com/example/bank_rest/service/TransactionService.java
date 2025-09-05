package com.example.bank_rest.service;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.enums.TransactionStatus;
import com.example.bank_rest.payload.dto.request.TransferRequestDto;
import com.example.bank_rest.payload.dto.response.TransactionResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface TransactionService {

    TransactionResponseDto transfer(TransferRequestDto transferRequestDto);

    Page<TransactionResponseDto> getMyTransactionsByStatus(TransactionStatus status, Pageable pageable);

    Page<TransactionResponseDto> getTransactionsByStatusForAdmin(TransactionStatus status, Pageable pageable);

}
