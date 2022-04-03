package com.zw.atmservice.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class BalanceCheckRequest {
    @NotNull
    private Long cardNumber;
    @NotNull
    private String pin;
}
