package com.bank.accountservice.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.transaction.DepositTransactionRecord;
import com.bank.accountservice.model.transaction.IncomingTransferTransactionRecord;
import com.bank.accountservice.model.transaction.OutgoingTransferTransactionRecord;
import com.bank.accountservice.model.transaction.TransactionRecord;
import com.bank.accountservice.repository.TransactionRepository;
import org.iban4j.Iban;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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

    @Test
    public void createAndSaveTransferTransaction_works() {
        //given
        final var transferRequest = TransferRequest.builder()
                .senderIban(Iban.random().toString())
                .receiverIban(Iban.random().toString())
                .amount(BigDecimal.TEN)
                .build();

        //when
        final var actual = transactionService.createAndSaveTransferTransaction(transferRequest);

        //then
        final var incomingTransaction = IncomingTransferTransactionRecord.builder()
                .ownerIban(transferRequest.getReceiverIban())
                .senderIban(transferRequest.getSenderIban())
                .amount(transferRequest.getAmount())
                .build();
        final var outgoingTransaction = OutgoingTransferTransactionRecord.builder()
                .ownerIban(transferRequest.getSenderIban())
                .receiverIban(transferRequest.getReceiverIban())
                .amount(transferRequest.getAmount())
                .build();
        assertThat(actual).isNotEmpty();
        assertThat(actual).usingElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(incomingTransaction, outgoingTransaction);
    }

    @Test
    public void findTransactionsByAccountIban_works() {
        //given
        final String ownerIban = Iban.random().toString();
        final var incomingTransaction = IncomingTransferTransactionRecord.builder()
                .ownerIban(ownerIban)
                .senderIban(Iban.random().toString())
                .amount(BigDecimal.TEN)
                .build();
        final var outgoingTransaction = OutgoingTransferTransactionRecord.builder()
                .ownerIban(ownerIban)
                .receiverIban(Iban.random().toString())
                .amount(BigDecimal.ONE)
                .build();
        final var depositTransaction = DepositTransactionRecord.builder()
                .ownerIban(ownerIban)
                .amount(BigDecimal.ZERO)
                .build();
        final var depositTransactionForSomeOtherAccount = DepositTransactionRecord.builder()
                .ownerIban(Iban.random().toString())
                .amount(BigDecimal.ZERO)
                .build();
        transactionRepository.saveAll(
                List.of(incomingTransaction,
                        outgoingTransaction,
                        depositTransaction,
                        depositTransactionForSomeOtherAccount));

        //when
        final Optional<List<TransactionRecord>> actualTransactions = transactionService.findTransactionsByAccountIban(incomingTransaction.getOwnerIban());

        //then
        assertThat(actualTransactions).isNotEmpty();
        assertThat(actualTransactions.get()).usingElementComparatorIgnoringFields("id")
                .containsExactlyInAnyOrder(incomingTransaction, outgoingTransaction, depositTransaction);
    }
}