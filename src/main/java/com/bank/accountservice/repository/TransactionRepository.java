package com.bank.accountservice.repository;

import com.bank.accountservice.model.transaction.TransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TransactionRepository extends JpaRepository<TransactionRecord, Integer> {
}
