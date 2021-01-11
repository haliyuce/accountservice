package com.bank.accountservice.model.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("OUTGOING")
@NoArgsConstructor
@Getter
public class OutgoingTransferTransactionRecord extends TransactionRecord{

    private String receiverIban;

    @Builder
    public OutgoingTransferTransactionRecord(final BigDecimal amount, final String ownerIban, final String receiverIban) {
        super(TransactionType.INCOMING, amount, ownerIban);
        this.receiverIban = receiverIban;
    }
}
