package com.example.bank_rest.entity;

import com.example.bank_rest.entity.enums.StatusCard;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Getter
@Setter
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Card {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(length = 16, unique = true)
    private String number;

    @ManyToOne(fetch = FetchType.LAZY)
    @ToString.Exclude
    private User user;

    private LocalDate expiryDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCard status;

    @Column(precision = 19, scale = 2, nullable = false)
    private BigDecimal balance = BigDecimal.ZERO;

    public Card(String name, String number, User user, LocalDate expiryDate, StatusCard status, BigDecimal balance) {
        this.name = name;
        this.number = number;
        this.user = user;
        this.expiryDate = expiryDate;
        this.status = status;
        this.balance = balance;
    }

}