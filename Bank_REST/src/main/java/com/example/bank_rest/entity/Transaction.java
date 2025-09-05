package com.example.bank_rest.entity;

import com.example.bank_rest.entity.enums.TransactionStatus;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Card sender;

    @ManyToOne
    private Card receiver;

    private BigDecimal amount;

    @Enumerated(EnumType.STRING)
    private TransactionStatus status; // SUCCESS, FAILED, PENDING

    private LocalDateTime createdAt;
}
