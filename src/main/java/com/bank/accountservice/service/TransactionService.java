package com.bank.accountservice.service;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.transaction.DepositTransactionRecord;
import com.bank.accountservice.model.transaction.IncomingTransferTransactionRecord;
import com.bank.accountservice.model.transaction.OutgoingTransferTransactionRecord;
import com.bank.accountservice.model.transaction.TransactionRecord;
import com.bank.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TransactionService {

    private final TransactionRepository transactionRepository;

    public TransactionRecord createAndSaveDepositTransaction(final DepositRequest depositRequest) {
        final var transaction = DepositTransactionRecord.builder()
                .amount(depositRequest.getAmount())
                .ownerIban(depositRequest.getIban())
                .build();
        return transactionRepository.save(transaction);
    }

    public List<TransactionRecord> createAndSaveTransferTransaction(final TransferRequest transferReq) {
        final var incomingTransaction = IncomingTransferTransactionRecord.builder()
                .ownerIban(transferReq.getReceiverIban())
                .senderIban(transferReq.getSenderIban())
                .amount(transferReq.getAmount())
                .build();
        final var outgoingTransaction = OutgoingTransferTransactionRecord.builder()
                .ownerIban(transferReq.getSenderIban())
                .receiverIban(transferReq.getReceiverIban())
                .amount(transferReq.getAmount())
                .build();
        return transactionRepository.saveAll(List.of(incomingTransaction, outgoingTransaction));
    }

    public Optional<List<TransactionRecord>> findTransactionsByAccountIban(final String ownerIban) {
        return transactionRepository.findAllByOwnerIban(ownerIban);
    }
}
