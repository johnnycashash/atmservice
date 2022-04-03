package com.zw.atmservice.account.service;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;

public interface AccountService {
    AccountDetail findAccountDetailByAccountNumber(Long accountNumber) throws AccountGeneralException;

    void updateAccountBalance(AccountDetail acccountDetail, long availableBalance, long availableOD) throws AccountGeneralException;
}
