package com.example.bank_rest.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InvalidCardOwnerException extends RuntimeException {
    public InvalidCardOwnerException(String message) {
        super(message);
    }
}
