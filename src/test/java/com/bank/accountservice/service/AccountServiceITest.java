package com.bank.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.bank.accountservice.exception.AccountNotFoundException;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.account.CheckedAccount;
import com.bank.accountservice.repository.AccountRepository;
import com.bank.accountservice.service.AccountService;
import com.bank.accountservice.service.TransactionService;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

@DataJpaTest
@Import({
        AccountService.class
})
public class AccountServiceITest {

    @Autowired
    private AccountService accountService;

    @Autowired
    private AccountRepository accountRepository;

    @MockBean
    private TransactionService transactionService;

    @Test
    public void deposit_works() {
        //given
        final var account = CheckedAccount.builder()
                .iban(Iban.random().toString())
                .balance(BigDecimal.ZERO)
                .customerId(1)
                .build();
        accountRepository.save(account);
        final var depositRequest = DepositRequest
                .builder()
                .amount(BigDecimal.TEN)
                .iban(account.getIban())
                .build();

        //when
        accountService.deposit(depositRequest);

        //then
        final var depositAccount = accountRepository.findById(account.getIban());
        assertThat(depositAccount).isNotEmpty();
        assertThat(depositAccount.get().getBalance()).isEqualTo(BigDecimal.TEN);
        verify(transactionService).createAndSaveDepositTransaction(depositRequest);
    }

    @Test
    public void deposit_fails_when_iban_cannot_found() {
        //given
        final var depositRequest = DepositRequest
                .builder()
                .amount(BigDecimal.TEN)
                .iban(Iban.random().toString())
                .build();

        //when
        assertThrows(AccountNotFoundException.class, () -> accountService.deposit(depositRequest));

        //then
    }

}