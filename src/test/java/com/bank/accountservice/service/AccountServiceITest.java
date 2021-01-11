package com.bank.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;

import com.bank.accountservice.exception.*;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.account.AccountType;
import com.bank.accountservice.model.account.CheckedAccount;
import com.bank.accountservice.model.account.LoanAccount;
import com.bank.accountservice.model.account.SavingAccount;
import com.bank.accountservice.repository.AccountRepository;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

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

    @Test
    public void getAccountsByTypes_works() {
        //given
        final var checkedAccount = CheckedAccount.builder()
                .balance(BigDecimal.ZERO)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var loanAccount = LoanAccount.builder()
                .balance(BigDecimal.ZERO)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var savingAccount = SavingAccount.builder()
                .balance(BigDecimal.ZERO)
                .customerId(1)
                .iban(Iban.random().toString())
                .boundCheckedAccountIban(checkedAccount.getIban())
                .build();
        accountRepository.saveAll(List.of(checkedAccount, loanAccount, savingAccount));
        final var accountTypes = Set.of(AccountType.CHECKED, AccountType.SAVING);

        //when
        final var actual = accountService.getAccountsByTypes(accountTypes);

        //then
        assertThat(actual).isNotEmpty();
        assertThat(actual.get()).containsExactlyInAnyOrder(checkedAccount, savingAccount);
    }

    @Test
    public void getCurrentBalance_works() {
        //given
        final var checkedAccount = CheckedAccount.builder()
                .balance(BigDecimal.TEN)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        accountRepository.save(checkedAccount);

        //when
        final var actual = accountService.getCurrentBalance(checkedAccount.getIban());

        //then
        assertThat(actual).isEqualTo(BigDecimal.TEN);
    }

    @Test
    public void getCurrentBalance_fails_when_iban_is_not_found() {
        //given

        //when
        assertThrows(
                AccountNotFoundException.class,
                () -> accountService.getCurrentBalance(Iban.random().toString()));

        //then
    }

    @Test
    public void transfer_works() {
        //given
        final var senderAccount = CheckedAccount.builder()
                .balance(BigDecimal.TEN)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var receiverAccount = CheckedAccount.builder()
                .balance(BigDecimal.ONE)
                .customerId(2)
                .iban(Iban.random().toString())
                .build();
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        final var transferReq = TransferRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .senderIban(senderAccount.getIban())
                .receiverIban(receiverAccount.getIban())
                .build();

        //when
        accountService.transfer(transferReq);

        //then
        final var senderAccountAfterTransfer = accountRepository.findById(senderAccount.getIban());
        assertThat(senderAccountAfterTransfer).isNotEmpty();
        assertThat(senderAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.valueOf(5));
        final var receiverAccountAfterTransfer = accountRepository.findById(receiverAccount.getIban());
        assertThat(receiverAccountAfterTransfer).isNotEmpty();
        assertThat(receiverAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.valueOf(6));
    }

    @Test
    public void transfer_fails_when_sender_has_insufficient_amounts() {
        //given
        final var senderAccount = CheckedAccount.builder()
                .balance(BigDecimal.ZERO)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var receiverAccount = CheckedAccount.builder()
                .balance(BigDecimal.ONE)
                .customerId(2)
                .iban(Iban.random().toString())
                .build();
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        final var transferReq = TransferRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .senderIban(senderAccount.getIban())
                .receiverIban(receiverAccount.getIban())
                .build();

        //when
        assertThrows(InsufficientFundsException.class, () -> accountService.transfer(transferReq));

        //then
        final var senderAccountAfterTransfer = accountRepository.findById(senderAccount.getIban());
        assertThat(senderAccountAfterTransfer).isNotEmpty();
        assertThat(senderAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.ZERO);
        final var receiverAccountAfterTransfer = accountRepository.findById(receiverAccount.getIban());
        assertThat(receiverAccountAfterTransfer).isNotEmpty();
        assertThat(receiverAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void transfer_fails_when_sender_is_loan_account() {
        //given
        final var senderAccount = LoanAccount.builder()
                .balance(BigDecimal.TEN)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var receiverAccount = CheckedAccount.builder()
                .balance(BigDecimal.ONE)
                .customerId(2)
                .iban(Iban.random().toString())
                .build();
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        final var transferReq = TransferRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .senderIban(senderAccount.getIban())
                .receiverIban(receiverAccount.getIban())
                .build();

        //when
        assertThrows(LoanAccountWithdrawException.class, () -> accountService.transfer(transferReq));

        //then
        final var senderAccountAfterTransfer = accountRepository.findById(senderAccount.getIban());
        assertThat(senderAccountAfterTransfer).isNotEmpty();
        assertThat(senderAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.TEN);
        final var receiverAccountAfterTransfer = accountRepository.findById(receiverAccount.getIban());
        assertThat(receiverAccountAfterTransfer).isNotEmpty();
        assertThat(receiverAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.ONE);
    }

    @Test
    public void transfer_fails_when_saving_account_sends_money_to_account_other_than_boundedCheckedAccount() {
        //given
        final var senderAccount = SavingAccount.builder()
                .balance(BigDecimal.TEN)
                .customerId(1)
                .iban(Iban.random().toString())
                .build();
        final var receiverAccount = CheckedAccount.builder()
                .balance(BigDecimal.ONE)
                .customerId(2)
                .iban(Iban.random().toString())
                .build();
        accountRepository.saveAll(List.of(senderAccount, receiverAccount));
        final var transferReq = TransferRequest.builder()
                .amount(BigDecimal.valueOf(5))
                .senderIban(senderAccount.getIban())
                .receiverIban(receiverAccount.getIban())
                .build();

        //when
        assertThrows(SavingAccountToUnboundAccountTransferException.class, () -> accountService.transfer(transferReq));

        //then
        final var senderAccountAfterTransfer = accountRepository.findById(senderAccount.getIban());
        assertThat(senderAccountAfterTransfer).isNotEmpty();
        assertThat(senderAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.TEN);
        final var receiverAccountAfterTransfer = accountRepository.findById(receiverAccount.getIban());
        assertThat(receiverAccountAfterTransfer).isNotEmpty();
        assertThat(receiverAccountAfterTransfer.get().getBalance()).isEqualTo(BigDecimal.ONE);
    }

}