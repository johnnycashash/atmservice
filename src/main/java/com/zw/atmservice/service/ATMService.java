package com.zw.atmservice.service;

import com.zw.atmservice.dto.BalanceCheckRequest;
import com.zw.atmservice.dto.BalanceCheckResponse;
import com.zw.atmservice.dto.WithdrawBalanceRequest;
import com.zw.atmservice.dto.WithdrawBalanceResponse;
import com.zw.atmservice.exception.ATMGeneralException;
import com.zw.atmservice.exception.InactiveAccountException;
import com.zw.atmservice.exception.InactiveCardException;
import com.zw.atmservice.exception.InvalidPinException;

public interface ATMService {
    BalanceCheckResponse getBalance(BalanceCheckRequest balanceCheckRequest) throws InvalidPinException, InactiveCardException, ATMGeneralException, InactiveAccountException;

    WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest withdrawBalanceRequest) throws InactiveCardException, InactiveAccountException, InvalidPinException;
}
