package com.example.bank_rest.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.LOCKED)
public class BlockedAccountException extends RuntimeException {
    public BlockedAccountException() {
        super("Account is blocked.");
    }
}