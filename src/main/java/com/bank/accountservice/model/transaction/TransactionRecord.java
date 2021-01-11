package com.bank.accountservice.model.transaction;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.math.BigDecimal;

@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
@Getter
@Setter
@NoArgsConstructor
public abstract class TransactionRecord {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private int id;
    @Enumerated(EnumType.STRING)
    private TransactionType transactionType;
    private BigDecimal amount;
    private String ownerIban;

    public TransactionRecord(TransactionType transactionType, BigDecimal amount, String ownerIban) {
        this.transactionType = transactionType;
        this.amount = amount;
        this.ownerIban = ownerIban;
    }
}
