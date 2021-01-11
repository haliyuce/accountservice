package com.bank.accountservice.model;

import com.bank.accountservice.validation.IbanValidation;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;

import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Builder
@EqualsAndHashCode
@Getter
public class TransferRequest {
    @NotNull
    private BigDecimal amount;
    @IbanValidation
    private String senderIban;
    @IbanValidation
    private String receiverIban;
}
