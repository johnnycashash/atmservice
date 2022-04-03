package com.zw.atmservice.atm.service;

import com.zw.atmservice.atm.dto.*;
import com.zw.atmservice.atm.exception.*;

public interface ATMService {
    BalanceCheckResponse getBalance(BalanceCheckRequest balanceCheckRequest) throws InvalidPinException, InactiveCardException, ATMGeneralException, InactiveAccountException;

    WithdrawBalanceResponse withdrawBalance(WithdrawBalanceRequest withdrawBalanceRequest) throws InactiveCardException, InactiveAccountException, InvalidPinException, InsufficientAmountException, AmountInDispensableException, ATMGeneralException;

    AtmInfoDetail checkAtm(Long atmId) throws ATMGeneralException;
}
