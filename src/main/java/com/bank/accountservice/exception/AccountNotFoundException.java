package com.bank.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class AccountNotFoundException extends ResponseStatusException {
    public AccountNotFoundException(String iban) {
        super(HttpStatus.BAD_REQUEST, "Account not found with this iban:" + iban);
    }
}
