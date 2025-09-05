package com.example.bank_rest.scheduler;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.enums.StatusCard;
import com.example.bank_rest.repository.CardRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class CardScheduler {

    private final CardRepository cardRepository;

    public CardScheduler(CardRepository cardRepository) {
        this.cardRepository = cardRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    @Transactional
    public void expireOldCards() {
        List<Card> cards = cardRepository.findAllByExpiryDateBeforeAndStatusNot(
                LocalDate.now(), StatusCard.EXPIRED);

        cards.forEach(card -> card.setStatus(StatusCard.EXPIRED));
        cardRepository.saveAll(cards);
    }
}
