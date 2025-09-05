package com.example.bank_rest.service;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.payload.dto.request.AddCardRequestDto;
import com.example.bank_rest.payload.dto.response.CardResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;

public interface CardService {

    CardResponseDto addCard(AddCardRequestDto addCardRequestDto);

    CardResponseDto getCardById(Long cardId);

    CardResponseDto getMyCard(Long cardId);

    Page<CardResponseDto> getMyCards(Pageable pageable, String filter);

    Page<CardResponseDto> getAllCards(StatusCard status, Pageable pageable);

    CardResponseDto blockCard(Long cardId);

    CardResponseDto activateCard(Long cardId);

    void deleteCard(Long cardId);

    CardResponseDto requestBlock(Long cardId);

    Card getCardOrThrow(Long cardId);

    void updateCardBalances(Card fromCard, Card toCard, BigDecimal amount);
}
