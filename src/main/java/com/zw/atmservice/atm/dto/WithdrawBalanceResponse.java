package com.zw.atmservice.atm.dto;

import lombok.Data;

import java.util.List;

@Data
public class WithdrawBalanceResponse {
    List<DenominationDetail> denominationDetails;
    Long remainingBalance;
    Long ramainingMaxBalance;
}
