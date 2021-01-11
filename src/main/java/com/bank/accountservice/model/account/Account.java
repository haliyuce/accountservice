package com.bank.accountservice.model.account;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.Objects;

@Entity
@Inheritance
@DiscriminatorColumn(name = "type")
@Getter
@Setter
@NoArgsConstructor
public abstract class Account {

    @Id
    private String iban;
    @NotNull
    private BigDecimal balance;
    private int customerId;
    @Column(insertable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    private AccountType type;
    @Version
    private int version;

    public Account(String iban, @NotNull BigDecimal balance, int customerId, AccountType type) {
        this.iban = iban;
        this.balance = balance;
        this.customerId = customerId;
        this.type = type;
    }

    @Override
    public boolean equals(Object accountObj) {
        if (this == accountObj) return true;
        if (accountObj == null || getClass() != accountObj.getClass()) return false;
        Account account = (Account) accountObj;
        return Objects.equals(iban, account.iban);
    }

    @Override
    public int hashCode() {
        return 12;
    }
}
