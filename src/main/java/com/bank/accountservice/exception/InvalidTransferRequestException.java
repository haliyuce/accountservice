package com.bank.accountservice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class InvalidTransferRequestException extends ResponseStatusException {
    public InvalidTransferRequestException(final String message) {
        super(HttpStatus.BAD_REQUEST, message);
    }
}
