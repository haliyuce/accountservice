package com.bank.accountservice.service;

import com.bank.accountservice.model.DepositRequest;
import com.bank.accountservice.model.TransferRequest;
import com.bank.accountservice.model.transaction.DepositTransactionRecord;
import com.bank.accountservice.model.transaction.TransactionRecord;
import com.bank.accountservice.repository.TransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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

    public void createAndSaveDepositTransaction(final TransferRequest transferReq) {

    }
}
