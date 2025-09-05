package com.example.bank_rest.service.impl;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.exps.RecordAlreadyException;
import com.example.bank_rest.exps.RecordNotFoundException;
import com.example.bank_rest.mapper.CardMapper;
import com.example.bank_rest.payload.dto.request.AddCardRequestDto;
import com.example.bank_rest.payload.dto.response.CardResponseDto;
import com.example.bank_rest.repository.CardRepository;
import com.example.bank_rest.service.CardService;
import com.example.bank_rest.service.UserService;
import com.example.bank_rest.validation.CardValidator;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Arrays;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final UserService userService;
    private final CardValidator cardValidator;
    private final CardMapper cardMapper;

    public CardServiceImpl(CardRepository cardRepository, UserService userService, CardValidator cardValidator, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.userService = userService;
        this.cardValidator = cardValidator;
        this.cardMapper = cardMapper;
    }

    @Override
    @Transactional
    public CardResponseDto addCard(AddCardRequestDto dto) {
        User user = userService.getCurrentUserId();

        try {
            Card card = Card.builder()
                    .name(dto.name())
                    .number(dto.number())
                    .balance(dto.balance())
                    .expiryDate(dto.expiryDate())
                    .status(StatusCard.ACTIVE)
                    .user(user)
                    .build();
            cardRepository.save(card);
            return cardMapper.toDto(card);

        } catch (DataIntegrityViolationException ex) {
            throw new RecordAlreadyException("This card number is already registered");
        }
    }

    @Override
    public CardResponseDto getCardById(Long cardId) {
        Card card = getCardOrThrow(cardId);

        return cardMapper.toDto(card);
    }

    @Override
    public CardResponseDto getMyCard(Long cardId) {
        User user = userService.getCurrentUserId();
        Card card = getCardOrThrow(cardId);

        cardValidator.checkCardOwnership(user, card);

        cardValidator.checkCardStatus(card);

        return cardMapper.toDto(card);
    }

    @Override
    public Page<CardResponseDto> getMyCards(Pageable pageable, String filter) {
        User user = userService.getCurrentUserId();
        Page<Card> cards;
        if (filter != null && !filter.isBlank()) {
            cards = cardRepository.findByUser_IdAndNameContainingIgnoreCase(user.getId(), filter, pageable);
        } else {
            cards = cardRepository.findByUser_Id(user.getId(), pageable);
        }
        return cards.map(cardMapper::toDto);
    }

    @Override
    public Page<CardResponseDto> getAllCards(StatusCard status, Pageable pageable) {
        Page<Card> cards;

        if (status == null) {
            cards = cardRepository.findAllByOrderByExpiryDateDesc(pageable);
        } else {
            cards = cardRepository.findAllByStatusOrderByExpiryDateDesc(status, pageable);
        }

        return cards.map(cardMapper::toDto);
    }


    @Override
    public CardResponseDto blockCard(Long cardId) {
        Card card = getCardOrThrow(cardId);

        card.setStatus(StatusCard.BLOCKED);

        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Override
    public CardResponseDto activateCard(Long cardId) {
        Card card = getCardOrThrow(cardId);

        card.setStatus(StatusCard.ACTIVE);

        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Override
    public void deleteCard(Long cardId) {
        Card card = getCardOrThrow(cardId);

        card.setStatus(StatusCard.DELETED);

        cardRepository.save(card);
    }

    @Override
    public CardResponseDto requestBlock(Long cardId) {
        User user = userService.getCurrentUserId();
        Card card = getCardOrThrow(cardId);

        cardValidator.checkCardOwnership(user, card);

        card.setStatus(StatusCard.PENDING_BLOCK);

        cardRepository.save(card);

        return cardMapper.toDto(card);
    }

    @Override
    public Card getCardOrThrow(Long cardId) {
        return cardRepository.findById(cardId)
                .orElseThrow(() -> new RecordNotFoundException("Card not found with id: " + cardId));
    }

    @Override
    @Transactional
    public void updateCardBalances(Card fromCard, Card toCard, BigDecimal amount) {
        fromCard.setBalance(fromCard.getBalance().subtract(amount));
        toCard.setBalance(toCard.getBalance().add(amount));

        cardRepository.saveAll(Arrays.asList(fromCard, toCard));
    }

}