package com.example.bank_rest.service;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.exps.RecordAlreadyException;
import com.example.bank_rest.exps.RecordNotFoundException;
import com.example.bank_rest.mapper.CardMapper;
import com.example.bank_rest.payload.dto.request.AddCardRequestDto;
import com.example.bank_rest.payload.dto.response.CardResponseDto;
import com.example.bank_rest.repository.CardRepository;
import com.example.bank_rest.service.impl.CardServiceImpl;
import com.example.bank_rest.validation.CardValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CardServiceTest {

    @Mock
    private CardRepository cardRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardValidator cardValidator;

    @Mock
    private CardMapper cardMapper;

    @InjectMocks
    private CardServiceImpl cardService;

    private User user;
    private Card card;
    private AddCardRequestDto addCardRequestDto;
    private CardResponseDto cardResponseDto;
    private Pageable pageable;

    @BeforeEach
    void setUp() {
        user = User.builder()
                .id(1L)
                .firstName("Ali")
                .lastName("Valiyev")
                .username("ali")
                .phoneNumber("+998901234567")
                .userStatus(UserStatus.ACTIVE)
                .password("encodedPassword")
                .build();

        card = Card.builder()
                .id(1L)
                .name("Test Card")
                .number("1234567890123456")
                .balance(new BigDecimal("1000.00"))
                .expiryDate(LocalDate.of(2026, 12, 31))
                .status(StatusCard.ACTIVE)
                .user(user)
                .build();

        addCardRequestDto = AddCardRequestDto.builder()
                .name("Test Card")
                .number("1234567890123456")
                .balance(new BigDecimal("1000.00"))
                .expiryDate(LocalDate.of(2026, 12, 31))
                .build();

        cardResponseDto = CardResponseDto.builder()
                .id(1L)
                .name("Test Card")
                .number("1234567890123456")
                .balance(new BigDecimal("1000.00"))
                .expiryDate(LocalDate.of(2026, 12, 31))
                .status(StatusCard.ACTIVE)
                .build();

        pageable = PageRequest.of(0, 10, Sort.by("expiryDate").descending());
    }

    @Test
    void addCard_Success() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardRepository.save(any(Card.class))).thenAnswer(invocation -> {
            Card savedCard = invocation.getArgument(0);
            savedCard.setId(1L);
            return savedCard;
        });
        when(cardMapper.toDto(any(Card.class))).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.addCard(addCardRequestDto);

        assertNotNull(result);
        assertEquals("1234567890123456", result.number());
        assertEquals("Test Card", result.name());
        assertEquals(new BigDecimal("1000.00"), result.balance());
        assertEquals(StatusCard.ACTIVE, result.status());
        verify(cardRepository).save(any(Card.class));
        verify(cardMapper).toDto(any(Card.class));
    }

    @Test
    void addCard_CardNumberAlreadyExists_ThrowsException() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardRepository.save(any(Card.class))).thenThrow(new DataIntegrityViolationException("Duplicate card number"));

        assertThrows(RecordAlreadyException.class, () -> cardService.addCard(addCardRequestDto));
        verify(cardMapper, never()).toDto(any(Card.class));
    }

    @Test
    void getCardById_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.getCardById(1L);

        assertNotNull(result);
        assertEquals("Test Card", result.name());
        verify(cardMapper).toDto(card);
    }

    @Test
    void getCardById_NotFound_ThrowsException() {
        when(cardRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RecordNotFoundException.class, () -> cardService.getCardById(1L));
        verify(cardMapper, never()).toDto(any(Card.class));
    }

    @Test
    void getMyCard_Success() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(cardValidator).checkCardOwnership(user, card);
        doNothing().when(cardValidator).checkCardStatus(card);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.getMyCard(1L);

        assertNotNull(result);
        assertEquals("1234567890123456", result.number());
        verify(cardValidator).checkCardOwnership(user, card);
        verify(cardValidator).checkCardStatus(card);
        verify(cardMapper).toDto(card);
    }

    @Test
    void getMyCards_Success_WithFilter() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardRepository.findByUser_IdAndNameContainingIgnoreCase(user.getId(), "Test", pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        Page<CardResponseDto> result = cardService.getMyCards(pageable, "Test");

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findByUser_IdAndNameContainingIgnoreCase(user.getId(), "Test", pageable);
    }

    @Test
    void getAllCards_Success_WithStatus() {
        Page<Card> cardPage = new PageImpl<>(List.of(card));
        when(cardRepository.findAllByStatusOrderByExpiryDateDesc(StatusCard.ACTIVE, pageable)).thenReturn(cardPage);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        Page<CardResponseDto> result = cardService.getAllCards(StatusCard.ACTIVE, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        verify(cardRepository).findAllByStatusOrderByExpiryDateDesc(StatusCard.ACTIVE, pageable);
    }

    @Test
    void blockCard_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.blockCard(1L);

        assertNotNull(result);
        assertEquals(StatusCard.BLOCKED, card.getStatus());
        verify(cardRepository).save(card);
        verify(cardMapper).toDto(card);
    }

    @Test
    void activateCard_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.activateCard(1L);

        assertNotNull(result);
        assertEquals(StatusCard.ACTIVE, card.getStatus());
        verify(cardRepository).save(card);
        verify(cardMapper).toDto(card);
    }

    @Test
    void deleteCard_Success() {
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        when(cardRepository.save(any(Card.class))).thenReturn(card);

        cardService.deleteCard(1L);

        assertEquals(StatusCard.DELETED, card.getStatus());
        verify(cardRepository).save(card);
    }

    @Test
    void requestBlock_Success() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardRepository.findById(1L)).thenReturn(Optional.of(card));
        doNothing().when(cardValidator).checkCardOwnership(user, card);
        when(cardRepository.save(any(Card.class))).thenReturn(card);
        when(cardMapper.toDto(card)).thenReturn(cardResponseDto);

        CardResponseDto result = cardService.requestBlock(1L);

        assertNotNull(result);
        assertEquals(StatusCard.PENDING_BLOCK, card.getStatus());
        verify(cardValidator).checkCardOwnership(user, card);
        verify(cardRepository).save(card);
        verify(cardMapper).toDto(card);
    }

    @Test
    void updateCardBalances_Success() {
        Card fromCard = Card.builder()
                .id(1L)
                .balance(new BigDecimal("1000.00"))
                .build();
        Card toCard = Card.builder()
                .id(2L)
                .balance(new BigDecimal("500.00"))
                .build();
        BigDecimal amount = new BigDecimal("200.00");

        when(cardRepository.saveAll(Arrays.asList(fromCard, toCard))).thenReturn(Arrays.asList(fromCard, toCard));

        cardService.updateCardBalances(fromCard, toCard, amount);

        assertEquals(new BigDecimal("800.00"), fromCard.getBalance());
        assertEquals(new BigDecimal("700.00"), toCard.getBalance());
        verify(cardRepository).saveAll(Arrays.asList(fromCard, toCard));
    }
}