package com.bank.accountservice.repository;

import com.bank.accountservice.model.transaction.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Integer> {
    Optional<List<TransactionRecord>> findAllByOwnerIban(String ownerIban);
}
