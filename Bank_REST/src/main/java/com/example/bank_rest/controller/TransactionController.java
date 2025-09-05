package com.example.bank_rest.controller;

import com.example.bank_rest.entity.enums.TransactionStatus;
import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import com.example.bank_rest.payload.dto.request.TransferRequestDto;
import com.example.bank_rest.payload.dto.response.TransactionResponseDto;
import com.example.bank_rest.service.TransactionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/transactions")
@Tag(name = "Transaction Controller")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping("/me/transfer")
    @Operation(summary = "User: Transfer money between own cards")
    public ApiResponse<TransactionResponseDto> transfer(@RequestBody @Valid TransferRequestDto dto) {
        return ApiResponseFactory.success(
                "Transfer completed",
                transactionService.transfer(dto)
        );
    }

    @GetMapping("/me")
    @Operation(summary = "User: Get own transactions (with optional status filter)")
    public ApiResponse<Page<TransactionResponseDto>> getMyTransactions(
            @RequestParam(required = false) TransactionStatus status,
            @ParameterObject Pageable pageable) {

        return ApiResponseFactory.success(
                "Your transactions",
                transactionService.getMyTransactionsByStatus(status, pageable)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Get all transactions")
    public ApiResponse<Page<TransactionResponseDto>> getAllTransactions(
            @RequestParam(required = false) TransactionStatus status,
            @ParameterObject Pageable pageable) {

        return ApiResponseFactory.success(
                "All transactions",
                transactionService.getTransactionsByStatusForAdmin(status, pageable)
        );
    }
}
