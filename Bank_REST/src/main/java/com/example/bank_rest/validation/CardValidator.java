package com.example.bank_rest.validation;

import com.example.bank_rest.entity.Card;
import com.example.bank_rest.entity.User;

public interface CardValidator {

    void checkCardOwnership(User user, Card card);

    void checkCardStatus(Card card);

}