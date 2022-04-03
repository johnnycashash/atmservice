package com.zw.atmservice.atm.service;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.atm.dto.DenominationDetail;
import com.zw.atmservice.atm.entity.ATMInfo;

import java.util.List;

public interface WithdrawUpdateService {
    void executeAccountAndAtmUpdate(AccountDetail accountDetailByAccountNumber, ATMInfo atmInfo,
                                    List<DenominationDetail> toBeUpdatedDtls, long availableBalance, long availableOD) throws AccountGeneralException;
}
