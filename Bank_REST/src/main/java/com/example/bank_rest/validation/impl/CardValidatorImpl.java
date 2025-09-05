package com.example.bank_rest.validation.impl;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.User;
import com.example.bank_rest.exps.CardNotActiveException;
import com.example.bank_rest.exps.InvalidCardOwnerException;
import com.example.bank_rest.validation.CardValidator;
import org.springframework.stereotype.Component;

@Component
public class CardValidatorImpl implements CardValidator {

    @Override
    public void checkCardOwnership(User user, Card card) {
        if (!card.getUser().getId().equals(user.getId())) {
            throw new InvalidCardOwnerException("You can only operate on your own card");
        }
    }

    @Override
    public void checkCardStatus(Card card) {
        if (!card.getStatus().isUsable()) {
            throw new CardNotActiveException("Card is not active: " + card.getStatus());
        }
    }

}