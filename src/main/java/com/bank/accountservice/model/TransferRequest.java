package com.bank.accountservice.model;

import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Builder
@Getter
public class TransferRequest {
    private BigDecimal amount;
    private String senderIban;
    private String receiverIban;
}
