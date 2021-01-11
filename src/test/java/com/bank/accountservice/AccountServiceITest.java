package com.bank.accountservice;

import static org.assertj.core.api.Assertions.assertThat;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.account.CheckedAccount;
import com.bank.accountservice.repository.AccountRepository;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
    }

}