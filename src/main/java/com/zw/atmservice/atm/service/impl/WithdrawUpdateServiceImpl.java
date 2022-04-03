package com.zw.atmservice.atm.service.impl;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.account.service.AccountService;
import com.zw.atmservice.atm.dao.ATMInfoRepository;
import com.zw.atmservice.atm.dto.DenominationDetail;
import com.zw.atmservice.atm.entity.ATMInfo;
import com.zw.atmservice.atm.service.WithdrawUpdateService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;


@Slf4j
@Service
public class WithdrawUpdateServiceImpl implements WithdrawUpdateService {


    @Autowired
    private AccountService accountService;

    @Autowired
    private ATMInfoRepository atmInfoRepository;

    @Override
    @Transactional
    public void executeAccountAndAtmUpdate(AccountDetail accountDetail, ATMInfo atmInfo,
                                           List<DenominationDetail> toBeUpdatedDtls, long availableBalance, long availableOD) throws AccountGeneralException {
        accountService.updateAccountBalance(accountDetail, availableBalance, availableOD);
        Map<Long, Long> denoValByCount = toBeUpdatedDtls.stream()
                .collect(Collectors.toMap(denominationDetail -> denominationDetail.getDenomination().getValue(), DenominationDetail::getCount));
        Set<Long> denoValByCountKeys = denoValByCount.keySet();
        atmInfo.getDenominationDetails().stream()
                .filter(denominationDetail -> denoValByCountKeys.contains(denominationDetail.getDenomination().getValue()))
                .forEach(denominationDetail -> denominationDetail.setCount(denoValByCount.get(denominationDetail.getDenomination().getValue())));
        atmInfoRepository.save(atmInfo);
    }
}
