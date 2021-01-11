package com.bank.accountservice.model.account;

import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("CHECKED")
public class CheckedAccount extends Account {

    public CheckedAccount() {
        super();
    }

    @Builder
    protected CheckedAccount(String iban, @NotNull BigDecimal balance, int customerId) {
        super(iban, balance, customerId, AccountType.CHECKED);
    }
}
