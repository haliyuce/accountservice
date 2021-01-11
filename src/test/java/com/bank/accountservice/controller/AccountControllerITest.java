package com.bank.accountservice.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.accountservice.exception.AccountNotFoundException;
import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.account.AccountType;
import com.bank.accountservice.model.account.SavingAccount;
import com.bank.accountservice.model.transaction.DepositTransactionRecord;
import com.bank.accountservice.model.transaction.IncomingTransferTransactionRecord;
import com.bank.accountservice.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.hamcrest.Matchers;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.Set;

@WebMvcTest
@Import({
        AccountController.class
})
@AutoConfigureMockMvc
public class AccountControllerITest {

    @MockBean
    private AccountService accountService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @SneakyThrows
    @Test
    public void deposit_works() {
        //given
        final var iban = Iban.random().toString();
        final var depositRequestJsonStr = "{" +
                "\"iban\":\"" + iban + "\"," +
                "\"amount\": 10" +
                "}";

        //when
        mockMvc.perform(post("/api/v1/accounts/deposit")
                .content(depositRequestJsonStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        //then
        final var expected = DepositRequest.builder()
                .amount(BigDecimal.TEN)
                .iban(iban)
                .build();
        verify(accountService).deposit(expected);
        verifyNoMoreInteractions(accountService);
    }

    @SneakyThrows
    @Test
    public void deposit_fails_when_iban_is_invalid() {
        //given
        final var depositRequestJsonStr = "{" +
                "\"iban\":\"some_wrong_iban\"," +
                "\"amount\": 10" +
                "}";

        //when
        mockMvc.perform(post("/api/v1/accounts/deposit")
                .content(depositRequestJsonStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(Matchers.containsString("Invalid IBAN number")));

        //then
        verifyNoInteractions(accountService);
    }

    @SneakyThrows
    @Test
    public void deposit_fails_when_service_throws_AccountNotFoundException() {
        //given
        final var iban = Iban.random().toString();
        final var depositRequestJsonStr = "{" +
                "\"iban\":\"" + iban + "\"," +
                "\"amount\": 10" +
                "}";
        doThrow(new AccountNotFoundException(iban))
                .when(accountService)
                .deposit(any(DepositRequest.class));

        //when
        mockMvc.perform(post("/api/v1/accounts/deposit")
                .content(depositRequestJsonStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isBadRequest())
                .andExpect(status().reason("Account not found with this iban:" + iban));

        //then
        final var expected = DepositRequest.builder()
                .amount(BigDecimal.TEN)
                .iban(iban)
                .build();
        verify(accountService).deposit(expected);
        verifyNoMoreInteractions(accountService);
    }

    @SneakyThrows
    @Test
    public void getAccountsByTypes_works() {
        //given
        var accountTypes = Set.of(AccountType.CHECKED, AccountType.SAVING);
        final var iban = Iban.random().toString();
        final var expected = List.of(
                SavingAccount.builder().balance(BigDecimal.TEN).customerId(1).iban(iban).build());
        doReturn(Optional.of(expected))
                .when(accountService)
                .getAccountsByTypes(anySet());
        final var expectedResponseStr = "[" +
                "{\"iban\":\"" + iban + "\"," +
                "\"balance\":10," +
                "\"customerId\":1," +
                "\"type\":\"SAVING\"," +
                "\"version\":0," +
                "\"boundCheckedAccountIban\":null}]";

        //when
        mockMvc.perform(get("/api/v1/accounts")
                .param("accountTypes", AccountType.LOAN.name(), AccountType.SAVING.name()))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseStr));

        //then
    }

    @SneakyThrows
    @Test
    public void getCurrentBalance_works() {
        //given
        final var iban = Iban.random().toString();
        doReturn(BigDecimal.TEN)
                .when(accountService)
                .getCurrentBalance(anyString());

        //when
        mockMvc.perform(get("/api/v1/accounts/" + iban + "/balance"))
                .andExpect(status().isOk())
                .andExpect(content().string("10"));

        //then
    }

    @SneakyThrows
    @Test
    public void transfer_works() {
        //given
        final var senderIban = Iban.random().toString();
        final var receiverIban = Iban.random().toString();
        final var requestStr = "{" +
                "\"amount\":10," +
                "\"receiverIban\": \"" + receiverIban + "\"," +
                "\"senderIban\": \"" + senderIban + "\"" +
                "}";

        //when
        mockMvc.perform(post("/api/v1/accounts/moneytransfer")
                .content(requestStr)
                .contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(status().isNoContent());

        //then
        final var expectedTransferReq = TransferRequest.builder()
                .receiverIban(receiverIban)
                .senderIban(senderIban)
                .amount(BigDecimal.TEN)
                .build();
        verify(accountService).transfer(expectedTransferReq);
        verifyNoMoreInteractions(accountService);
    }

    @SneakyThrows
    @Test
    public void getTransactions_works() {
        //given
        final var iban = Iban.random().toString();
        final var transactions = List.of(
                DepositTransactionRecord.builder().ownerIban(iban).amount(BigDecimal.TEN).build(),
                IncomingTransferTransactionRecord.builder().amount(BigDecimal.TEN).ownerIban(iban).build()
        );

        doReturn(Optional.of(transactions))
                .when(accountService)
                .getTransactions(iban);

        final var expectedResponseJsonStr = "[" +
                    "{" +
                    "   \"id\":0," +
                    "   \"transactionType\":\"DEPOSIT\"," +
                    "   \"amount\":10," +
                    "   \"ownerIban\":\"" + iban + "\"" +
                    "}," +
                    "{" +
                    "   \"id\":0," +
                    "   \"transactionType\":\"INCOMING\"," +
                    "   \"amount\":10," +
                    "   \"ownerIban\":\"" + iban + "\"," +
                    "   \"senderIban\":null" +
                    "}" +
                "]";

        //when
        mockMvc.perform(get("/api/v1/accounts/" + iban + "/transactions"))
                .andExpect(status().isOk())
                .andExpect(content().json(expectedResponseJsonStr));

        //then
    }

    //TODO: check the cases for
    // InsufficientFundsException, InvalidTransferRequestException, LoanAccountWithdrawException,
    // SavingAccountToUnboundAccountTransferException and invalid Iban checks
}