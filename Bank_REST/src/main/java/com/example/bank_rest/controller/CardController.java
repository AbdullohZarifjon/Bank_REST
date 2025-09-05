package com.example.bank_rest.controller;

import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.payload.common.ApiResponse;
import com.example.bank_rest.payload.common.ApiResponseFactory;
import com.example.bank_rest.payload.dto.request.AddCardRequestDto;
import com.example.bank_rest.payload.dto.response.CardResponseDto;
import com.example.bank_rest.service.CardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequestMapping("/api/cards")
@Tag(name = "Card Controller")
public class CardController {

    private final CardService cardService;

    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/me")
    @Operation(summary = "User: Add a new card")
    public ApiResponse<CardResponseDto> addCard(@RequestBody @Valid AddCardRequestDto dto) {
        return ApiResponseFactory.created("Card successfully added", cardService.addCard(dto));
    }

    @GetMapping("/{cardId}")
    @Operation(summary = "Admin: Get card by ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ApiResponse<CardResponseDto> getCardById(@PathVariable @Min(1) Long cardId) {
        return ApiResponseFactory.success("Card found", cardService.getCardById(cardId));
    }

    @GetMapping("/me/{cardId}")
    @Operation(summary = "User: Get own card by ID")
    public ApiResponse<CardResponseDto> getMyCard(@PathVariable @Min(1) Long cardId) {
        return ApiResponseFactory.success("Card found", cardService.getMyCard(cardId));
    }

    @GetMapping("/me")
    @Operation(summary = "User: Get all own cards")
    public ApiResponse<Page<CardResponseDto>> getMyCards(@ParameterObject Pageable pageable,
                                                         @RequestParam(required = false) String filter) {
        return ApiResponseFactory.success("Your cards list", cardService.getMyCards(pageable, filter));
    }

    @GetMapping
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Get all cards with optional status filter")
    public ApiResponse<Page<CardResponseDto>> getAllCards(
            @RequestParam(required = false) StatusCard status,
            @ParameterObject Pageable pageable) {
        return ApiResponseFactory.success("Cards fetched",
                cardService.getAllCards(status, pageable));
    }

    @PutMapping("/{cardId}/block")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Block a card")
    public ApiResponse<CardResponseDto> blockCard(@PathVariable @Min(1) Long cardId) {
        return ApiResponseFactory.success("Card blocked", cardService.blockCard(cardId));
    }

    @PutMapping("/{cardId}/activate")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Activate a card")
    public ApiResponse<CardResponseDto> activateCard(@PathVariable @Min(1) Long cardId) {
        return ApiResponseFactory.success("Card activated", cardService.activateCard(cardId));
    }

    @DeleteMapping("/{cardId}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @Operation(summary = "Admin: Delete a card")
    public ApiResponse<Void> deleteCard(@PathVariable @Min(1) Long cardId) {
        cardService.deleteCard(cardId);
        return ApiResponseFactory.noContent("Card deleted");
    }

    @PutMapping("/me/{cardId}/request-block")
    @Operation(summary = "User: Request to block own card")
    public ApiResponse<CardResponseDto> requestBlockCard(@PathVariable @Min(1) Long cardId) {
        return ApiResponseFactory.success("Card block requested", cardService.requestBlock(cardId));
    }


}
