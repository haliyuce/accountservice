package com.bank.accountservice.model.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("INCOMING")
@NoArgsConstructor
@Getter
public class IncomingTransferTransactionRecord extends TransactionRecord{

    private String senderIban;

    @Builder
    public IncomingTransferTransactionRecord(final BigDecimal amount, final String ownerIban, final String senderIban) {
        super(TransactionType.INCOMING, amount, ownerIban);
        this.senderIban = senderIban;
    }
}
