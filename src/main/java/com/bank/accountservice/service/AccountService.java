package com.bank.accountservice.service;

import com.bank.accountservice.exception.*;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.account.Account;
import com.bank.accountservice.model.account.AccountType;
import com.bank.accountservice.model.account.SavingAccount;
import com.bank.accountservice.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Transactional
    public void transfer(final TransferRequest transferReq) {
        final Account senderAccount = getAccount(transferReq.getSenderIban());
        final Account receiverAccount = getAccount(transferReq.getReceiverIban());
        validateTransfer(senderAccount, receiverAccount, transferReq);
        setNewBalancesAndSave(transferReq.getAmount(), senderAccount, receiverAccount);
        transactionService.createAndSaveDepositTransaction(transferReq);
    }

    private void setNewBalancesAndSave(BigDecimal amount, Account senderAccount, Account receiverAccount) {
        senderAccount.setBalance(senderAccount.getBalance().subtract(amount));
        receiverAccount.setBalance(receiverAccount.getBalance().add(amount));
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
    }

    private void validateTransfer(Account senderAccount, Account receiverAccount, TransferRequest transferReq) {
        if (senderAccount.getBalance().compareTo(transferReq.getAmount()) < 1) {
            throw new InsufficientFundsException();
        }
        if (AccountType.LOAN.equals(senderAccount.getType())) {
            throw new LoanAccountWithdrawException();
        }
        if (AccountType.SAVING.equals(senderAccount.getType())) {
            final var savingSenderAccount = (SavingAccount) senderAccount;
            if (!StringUtils.hasText(savingSenderAccount.getBoundCheckedAccountIban())
                    || !savingSenderAccount.getBoundCheckedAccountIban().equals(receiverAccount.getIban())) {
                throw new SavingAccountToUnboundAccountTransferException();
            }
        }
    }
}
