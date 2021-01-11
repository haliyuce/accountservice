package com.bank.accountservice.model.account;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("SAVING")
@Getter
@Setter
public class SavingAccount extends Account {

    private String boundCheckedAccountIban;

    public SavingAccount() {
        super();
    }

    public SavingAccount(String iban, @NotNull BigDecimal balance, int customerId, String boundCheckedAccountIban) {
        super(iban, balance, customerId, AccountType.SAVING);
        this.boundCheckedAccountIban = boundCheckedAccountIban;
    }
}
