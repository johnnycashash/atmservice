package com.zw.atmservice.service;

import com.zw.atmservice.dto.AccountDetail;
import com.zw.atmservice.exception.AccountGeneralException;
import com.zw.atmservice.exception.AccountNumberInvalidException;

public interface AccountService {
    AccountDetail findAccountDetailByAccountNumber(Long accountNumber) throws AccountNumberInvalidException, AccountGeneralException;

    boolean updateAccountBalance(AccountDetail acccountDetail, long availableBalance, long availableOD);
}
