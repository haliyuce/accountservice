package com.bank.accountservice.exception;

public class SavingAccountToUnboundAccountTransferException extends InvalidTransferRequestException {
    public SavingAccountToUnboundAccountTransferException() {
        super("Saving account cannot send money to an account which is not it's bounded checked account");
    }
}
