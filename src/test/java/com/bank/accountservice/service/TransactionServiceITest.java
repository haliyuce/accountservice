package com.bank.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.transaction.DepositTransactionRecord;
import com.bank.accountservice.repository.TransactionRepository;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({
        TransactionService.class
})
public class TransactionServiceITest {

    @Autowired
    private TransactionService transactionService;
    @Autowired
    private TransactionRepository transactionRepository;

    @Test
    public void createAndSaveDepositTransaction_works() {
        //given
        final var depositRequest =  DepositRequest.builder()
                .iban(Iban.random().toString())
                .amount(BigDecimal.TEN)
                .build();

        //when
        final var actual = transactionService.createAndSaveDepositTransaction(depositRequest);

        //then
        final var actualTrRecord = transactionRepository.findById(actual.getId());
        assertThat(actualTrRecord).isNotEmpty();
        final var expected = DepositTransactionRecord.builder()
                .ownerIban(depositRequest.getIban())
                .amount(depositRequest.getAmount())
                .build();
        assertThat(actualTrRecord.get()).usingRecursiveComparison().ignoringFields("id").isEqualTo(expected);
    }
}