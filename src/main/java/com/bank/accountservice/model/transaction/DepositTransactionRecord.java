package com.bank.accountservice.model.transaction;

import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("DEPOSIT")
public class DepositTransactionRecord extends TransactionRecord {

    public DepositTransactionRecord() {
    }

    @Builder
    protected DepositTransactionRecord(final BigDecimal amount, final String ownerIban) {
        super(TransactionType.DEPOSIT, amount, ownerIban);
    }
}
