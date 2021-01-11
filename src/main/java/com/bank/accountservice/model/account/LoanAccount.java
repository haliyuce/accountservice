package com.bank.accountservice.model.account;

import lombok.Builder;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@DiscriminatorValue("LOAN")
public class LoanAccount extends Account {

    public LoanAccount() {
        super();
    }

    @Builder
    protected LoanAccount(String iban, @NotNull BigDecimal balance, int customerId) {
        super(iban, balance, customerId, AccountType.LOAN);
    }
}
