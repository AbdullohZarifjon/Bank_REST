package com.example.bank_rest.service;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.Transaction;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.entity.enums.TransactionStatus;
import com.example.bank_rest.entity.enums.UserStatus;
import com.example.bank_rest.exps.InsufficientBalanceException;
import com.example.bank_rest.exps.RecordNotFoundException;
import com.example.bank_rest.mapper.TransactionMapper;
import com.example.bank_rest.payload.dto.request.TransferRequestDto;
import com.example.bank_rest.payload.dto.response.TransactionResponseDto;
import com.example.bank_rest.repository.TransactionRepository;
import com.example.bank_rest.service.impl.TransactionServiceImpl;
import com.example.bank_rest.validation.CardValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @Mock
    private TransactionRepository transactionRepository;

    @Mock
    private UserService userService;

    @Mock
    private CardService cardService;

    @Mock
    private CardValidator cardValidator;

    @Mock
    private TransactionMapper transactionMapper;

    @InjectMocks
    private TransactionServiceImpl transactionService;

    private User user;
    private Card fromCard;
    private Card toCard;
    private Transaction transaction;
    private TransferRequestDto transferRequestDto;
    private TransactionResponseDto transactionResponseDto;
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

        fromCard = Card.builder()
                .id(1L)
                .name("Sender Card")
                .number("1234567890123456")
                .balance(new BigDecimal("1000.00"))
                .expiryDate(LocalDate.of(2026, 12, 22))
                .status(StatusCard.ACTIVE)
                .user(user)
                .build();

        toCard = Card.builder()
                .id(2L)
                .name("Receiver Card")
                .number("6543210987654321")
                .balance(new BigDecimal("500.00"))
                .expiryDate(LocalDate.of(2026, 12, 31))
                .status(StatusCard.ACTIVE)
                .user(user)
                .build();

        transaction = Transaction.builder()
                .id(1L)
                .sender(fromCard)
                .receiver(toCard)
                .amount(new BigDecimal("200.00"))
                .status(TransactionStatus.SUCCESS)
                .createdAt(LocalDateTime.now())
                .build();

        transferRequestDto = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(new BigDecimal("200.00"))
                .build();

        transactionResponseDto = TransactionResponseDto.builder()
                .id(1L)
                .fromCard("1234567890123456")
                .toCard("6543210987654321")
                .amount(new BigDecimal("200.00"))
                .status(TransactionStatus.SUCCESS.name())
                .createdAt(LocalDateTime.now())
                .build();

        pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
    }

    @Test
    void transfer_Success() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardService.getCardOrThrow(1L)).thenReturn(fromCard);
        when(cardService.getCardOrThrow(2L)).thenReturn(toCard);
        doNothing().when(userService).validateUserStatus(user);
        doNothing().when(cardValidator).checkCardOwnership(user, fromCard);
        doNothing().when(cardValidator).checkCardOwnership(user, toCard);
        doNothing().when(cardValidator).checkCardStatus(fromCard);
        doNothing().when(cardValidator).checkCardStatus(toCard);
        doNothing().when(cardService).updateCardBalances(fromCard, toCard, transferRequestDto.amount());
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionResponseDto);

        TransactionResponseDto result = transactionService.transfer(transferRequestDto);

        assertNotNull(result);
        assertEquals("1234567890123456", result.fromCard());
        assertEquals("6543210987654321", result.toCard());
        assertEquals(new BigDecimal("200.00"), result.amount());
        assertEquals(TransactionStatus.SUCCESS.name(), result.status());
        verify(cardService).updateCardBalances(fromCard, toCard, transferRequestDto.amount());
        verify(transactionRepository).save(any(Transaction.class));
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void transfer_InsufficientBalance_ThrowsException() {
        TransferRequestDto invalidTransferDto = TransferRequestDto.builder()
                .fromCardId(1L)
                .toCardId(2L)
                .amount(new BigDecimal("2000.00"))
                .build();

        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardService.getCardOrThrow(1L)).thenReturn(fromCard);
        when(cardService.getCardOrThrow(2L)).thenReturn(toCard);
        doNothing().when(userService).validateUserStatus(user);
        doNothing().when(cardValidator).checkCardOwnership(user, fromCard);
        doNothing().when(cardValidator).checkCardOwnership(user, toCard);
        doNothing().when(cardValidator).checkCardStatus(fromCard);
        doNothing().when(cardValidator).checkCardStatus(toCard);
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        assertThrows(InsufficientBalanceException.class, () -> transactionService.transfer(invalidTransferDto));
        verify(transactionRepository).save(any(Transaction.class));
        verify(cardService, never()).updateCardBalances(any(), any(), any());
        verify(transactionMapper, never()).toDto(any());
    }

    @Test
    void transfer_CardNotFound_ThrowsException() {
        when(userService.getCurrentUserId()).thenReturn(user);
        when(cardService.getCardOrThrow(1L)).thenThrow(new RecordNotFoundException("Card not found with id: 1"));

        assertThrows(RecordNotFoundException.class, () -> transactionService.transfer(transferRequestDto));
        verify(transactionRepository, never()).save(any(Transaction.class));
        verify(cardService, never()).updateCardBalances(any(), any(), any());
    }

    @Test
    void getMyTransactionsByStatus_Success_WithStatus() {
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(userService.getCurrentUserId()).thenReturn(user);
        when(transactionRepository.findAllBySender_User_IdOrReceiver_User_IdAndStatusOrderByCreatedAtDesc(
                user.getId(), user.getId(), TransactionStatus.SUCCESS, pageable)).thenReturn(transactionPage);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionResponseDto);

        Page<TransactionResponseDto> result = transactionService.getMyTransactionsByStatus(TransactionStatus.SUCCESS, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("1234567890123456", result.getContent().get(0).fromCard());
        verify(transactionRepository).findAllBySender_User_IdOrReceiver_User_IdAndStatusOrderByCreatedAtDesc(
                user.getId(), user.getId(), TransactionStatus.SUCCESS, pageable);
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void getMyTransactionsByStatus_Success_WithoutStatus() {
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(userService.getCurrentUserId()).thenReturn(user);
        when(transactionRepository.findAllBySender_User_IdOrReceiver_User_IdOrderByCreatedAtDesc(
                user.getId(), user.getId(), pageable)).thenReturn(transactionPage);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionResponseDto);

        Page<TransactionResponseDto> result = transactionService.getMyTransactionsByStatus(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("1234567890123456", result.getContent().get(0).fromCard());
        verify(transactionRepository).findAllBySender_User_IdOrReceiver_User_IdOrderByCreatedAtDesc(
                user.getId(), user.getId(), pageable);
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void getTransactionsByStatusForAdmin_Success_WithStatus() {
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAllByStatusOrderByCreatedAtDesc(TransactionStatus.SUCCESS, pageable))
                .thenReturn(transactionPage);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionResponseDto);

        Page<TransactionResponseDto> result = transactionService.getTransactionsByStatusForAdmin(TransactionStatus.SUCCESS, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("1234567890123456", result.getContent().get(0).fromCard());
        verify(transactionRepository).findAllByStatusOrderByCreatedAtDesc(TransactionStatus.SUCCESS, pageable);
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void getTransactionsByStatusForAdmin_Success_WithoutStatus() {
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction));
        when(transactionRepository.findAllByOrderByCreatedAtDesc(pageable)).thenReturn(transactionPage);
        when(transactionMapper.toDto(transaction)).thenReturn(transactionResponseDto);

        Page<TransactionResponseDto> result = transactionService.getTransactionsByStatusForAdmin(null, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
        assertEquals("1234567890123456", result.getContent().get(0).fromCard());
        verify(transactionRepository).findAllByOrderByCreatedAtDesc(pageable);
        verify(transactionMapper).toDto(transaction);
    }

    @Test
    void validateSufficientBalance_Success() {
        BigDecimal amount = new BigDecimal("500.00");

        assertDoesNotThrow(() -> transactionService.validateSufficientBalance(fromCard, toCard, amount));
        verify(transactionRepository, never()).save(any(Transaction.class));
    }

    @Test
    void validateSufficientBalance_InsufficientBalance_ThrowsException() {
        BigDecimal amount = new BigDecimal("2000.00");
        when(transactionRepository.save(any(Transaction.class))).thenReturn(transaction);

        assertThrows(InsufficientBalanceException.class, () -> transactionService.validateSufficientBalance(fromCard, toCard, amount));
        verify(transactionRepository).save(any(Transaction.class));
    }
}