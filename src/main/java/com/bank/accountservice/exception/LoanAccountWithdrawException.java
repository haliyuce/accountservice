package com.bank.accountservice.exception;

public class LoanAccountWithdrawException extends InvalidTransferRequestException {

    public LoanAccountWithdrawException() {
        super("Sender account cannot be a loan account");
    }
}
