package com.zw.atmservice.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class WithdrawBalanceRequest {
    @NotNull
    private Long atmId;
    @NotNull
    private Long cardNumber;
    @NotNull
    private String pin;
    @NotNull
    private Long amount;
}
