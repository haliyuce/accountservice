package com.bank.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Builder
@Getter
public class DepositRequest {
    private String iban;
    private BigDecimal amount;
}
