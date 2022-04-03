package com.zw.atmservice.atm.service;

import com.zw.atmservice.atm.dto.BalanceCheckRequest;
import com.zw.atmservice.atm.dto.BalanceCheckResponse;
import com.zw.atmservice.atm.dto.WithdrawBalanceRequest;
import com.zw.atmservice.atm.dto.WithdrawBalanceResponse;
import com.zw.atmservice.atm.exception.*;

public interface ATMService {
    BalanceCheckResponse getBalance(BalanceCheckRequest balanceCheckRequest) throws InvalidPinException, InactiveCardException, ATMGeneralException, InactiveAccountException;

    WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest withdrawBalanceRequest) throws InactiveCardException, InactiveAccountException, InvalidPinException, InsufficientAmountException, AmountInDispensableException, ATMGeneralException;
}
