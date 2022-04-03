package com.zw.atmservice.dto;

import lombok.Data;

@Data
public class BalanceCheckResponse {
    Long balance;
    Long maxBalance;
}
