package com.zw.atmservice.atm.dto;

import lombok.Data;

@Data
public class BalanceCheckResponse {
    Long balance;
    Long maxBalance;
}
