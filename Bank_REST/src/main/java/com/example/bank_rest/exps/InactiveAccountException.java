package com.example.bank_rest.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.FORBIDDEN)
public class InactiveAccountException extends RuntimeException {
    public InactiveAccountException() {
        super("Account is inactive. Please contact support.");
    }
}