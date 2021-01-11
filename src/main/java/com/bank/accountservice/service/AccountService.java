package com.bank.accountservice.service;

import com.bank.accountservice.exception.AccountNotFoundException;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountType;
import com.bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@RequiredArgsConstructor
@Service
public class AccountService {

    private final AccountRepository accountRepository;
    private final TransactionService transactionService;

    @Transactional
    public void deposit(final DepositRequest depositRequest) {
        final Account account = getAccount(depositRequest.getIban());
        account.setBalance(account.getBalance().add(depositRequest.getAmount()));
        accountRepository.save(account);
        transactionService.createAndSaveDepositTransaction(depositRequest);
    }

    public Optional<List<Account>> getAccountsByTypes(final Set<AccountType> accountTypes) {
        return accountRepository.findAllByTypeIn(accountTypes);
    }

    public BigDecimal getCurrentBalance(final String iban) {
        final var account = getAccount(iban);
        return account.getBalance();
    }

    private Account getAccount(final String iban) {
        final var account = accountRepository
                .findById(iban)
                .orElseThrow(() -> new AccountNotFoundException(iban));
        return account;
    }
}
