package com.bank.accountservice.service;

import com.bank.accountservice.exception.AccountNotFoundException;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Transactional
    public void deposit(final DepositRequest depositRequest) {
        final var account = accountRepository
                .findById(depositRequest.getIban())
                .orElseThrow(() -> new AccountNotFoundException(depositRequest.getIban()));
        account.setBalance(account.getBalance().add(depositRequest.getAmount()));
        accountRepository.save(account);
        transactionService.createAndSaveDepositTransaction(depositRequest);
    }
}
