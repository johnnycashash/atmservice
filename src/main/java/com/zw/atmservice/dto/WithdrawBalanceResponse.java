package com.zw.atmservice.dto;

import lombok.Data;

import java.util.List;

@Data
public class WithdrawBalanceResponse {
    List<DenominationDetail> denominationDetails;
    Long remainingBalance;
    Long ramainingMaxBalance;
}
