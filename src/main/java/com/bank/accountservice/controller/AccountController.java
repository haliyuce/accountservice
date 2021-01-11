package com.bank.accountservice.controller;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountType;
import com.bank.accountservice.model.transaction.TransactionRecord;
import com.bank.accountservice.service.AccountService;
import com.bank.accountservice.validation.IbanValidation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @PostMapping("/deposit")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deposit(@Valid @RequestBody final DepositRequest depositRequest) {
        accountService.deposit(depositRequest);
    }

    @GetMapping
    public Optional<List<Account>> getAccountsByTypes(@RequestParam @NotNull Set<AccountType> accountTypes) {
        return accountService.getAccountsByTypes(accountTypes);
    }

    @GetMapping("/{iban}/balance")
    public BigDecimal getCurrentBalance(@PathVariable
                                            @Valid
                                            @IbanValidation final String iban) {
        return accountService.getCurrentBalance(iban);
    }

    @PostMapping("/moneytransfer")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void transfer(@Valid @RequestBody TransferRequest transferRequest) {
        accountService.transfer(transferRequest);
    }

    @GetMapping("/{iban}/transactions")
    public Optional<List<TransactionRecord>> getTransactions(@PathVariable
                                        @Valid
                                        @IbanValidation final String iban) {
        return accountService.getTransactions(iban);
    }
}
