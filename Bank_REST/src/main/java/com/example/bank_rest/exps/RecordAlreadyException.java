package com.example.bank_rest.exps;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.CONFLICT)
public class RecordAlreadyException extends RuntimeException {

    public RecordAlreadyException(String message) {
        super(message);
    }
}
