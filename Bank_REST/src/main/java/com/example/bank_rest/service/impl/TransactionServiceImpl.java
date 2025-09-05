package com.example.bank_rest.service.impl;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.Transaction;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.entity.enums.TransactionStatus;
import com.example.bank_rest.exps.InsufficientBalanceException;
import com.example.bank_rest.mapper.TransactionMapper;
import com.example.bank_rest.payload.dto.request.TransferRequestDto;
import com.example.bank_rest.payload.dto.response.TransactionResponseDto;
import com.example.bank_rest.repository.TransactionRepository;
import com.example.bank_rest.service.CardService;
import com.example.bank_rest.service.TransactionService;
import com.example.bank_rest.service.UserService;
import com.example.bank_rest.validation.CardValidator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final UserService userService;
    private final CardService cardService;
    private final CardValidator cardValidator;
    private final TransactionMapper transactionMapper;

    public TransactionServiceImpl(TransactionRepository transactionRepository, UserService userService, CardService cardService, CardValidator cardValidator, TransactionMapper transactionMapper) {
        this.transactionRepository = transactionRepository;
        this.userService = userService;
        this.cardService = cardService;
        this.cardValidator = cardValidator;
        this.transactionMapper = transactionMapper;
    }

    @Override
    @Transactional
    public TransactionResponseDto transfer(TransferRequestDto transferRequestDto) {
        User user = userService.getCurrentUserId();
        Card fromCard = cardService.getCardOrThrow(transferRequestDto.fromCardId());
        Card toCard = cardService.getCardOrThrow(transferRequestDto.toCardId());

        BigDecimal amount = transferRequestDto.amount();

        userService.validateUserStatus(user);

        cardValidator.checkCardOwnership(user, fromCard);
        cardValidator.checkCardOwnership(user, toCard);

        cardValidator.checkCardStatus(fromCard);
        cardValidator.checkCardStatus(toCard);

        validateSufficientBalance(fromCard, toCard, amount);

        cardService.updateCardBalances(fromCard, toCard, amount);

        Transaction transaction = saveTransaction(fromCard, toCard, amount, TransactionStatus.SUCCESS);

        return transactionMapper.toDto(transaction);
    }

    @Override
    public Page<TransactionResponseDto> getMyTransactionsByStatus(TransactionStatus status, Pageable pageable) {
        User user = userService.getCurrentUserId();

        Page<Transaction> transactions;
        Long userId = user.getId();
        if (status == null) {
            transactions = transactionRepository
                    .findAllBySender_User_IdOrReceiver_User_IdOrderByCreatedAtDesc(userId, userId, pageable);
        } else {
            transactions = transactionRepository
                    .findAllBySender_User_IdOrReceiver_User_IdAndStatusOrderByCreatedAtDesc(
                            userId, userId, status, pageable
                    );
        }

        return transactions.map(transactionMapper::toDto);
    }

    @Override
    public Page<TransactionResponseDto> getTransactionsByStatusForAdmin(TransactionStatus status, Pageable pageable) {
        Page<Transaction> transactions;

        if (status == null) {
            transactions = transactionRepository.findAllByOrderByCreatedAtDesc(pageable);
        } else {
            transactions = transactionRepository.findAllByStatusOrderByCreatedAtDesc(status, pageable);
        }

        return transactions.map(transactionMapper::toDto);
    }


    public void validateSufficientBalance(Card fromCard, Card toCard, BigDecimal amount) {
        if (fromCard.getBalance().compareTo(amount) < 0) {
            saveTransaction(fromCard, toCard, amount, TransactionStatus.FAILED);
            throw new InsufficientBalanceException("Insufficient balance for transfer");
        }
    }

    private Transaction saveTransaction(Card sender, Card receiver, BigDecimal amount, TransactionStatus status) {
        Transaction transaction = Transaction.builder()
                .sender(sender)
                .receiver(receiver)
                .amount(amount)
                .status(status)
                .createdAt(LocalDateTime.now())
                .build();
        return transactionRepository.save(transaction);
    }
}
