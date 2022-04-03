package com.zw.atmservice.service.impl;

import com.zw.atmservice.dao.AccountRepository;
import com.zw.atmservice.dto.AccountDetail;
import com.zw.atmservice.entity.Account;
import com.zw.atmservice.exception.AccountGeneralException;
import com.zw.atmservice.exception.AccountNumberInvalidException;
import com.zw.atmservice.service.AccountService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountDetail findAccountDetailByAccountNumber(Long accountNumber) throws AccountNumberInvalidException, AccountGeneralException {
        Optional<Account> account = accountRepository.findById(accountNumber);
        if (account.isPresent()) {
            AccountDetail target = new AccountDetail();
            BeanUtils.copyProperties(account.get(), target);
            return target;
        } else {
            throw new AccountNumberInvalidException("Account number not found");
        }

    }

    @Override
    public boolean updateAccountBalance(AccountDetail acccountDetail, long availableBalance, long availableOD) {
        Account account = new Account();
        BeanUtils.copyProperties(acccountDetail, account);
        account.setBalance(availableBalance);
        account.setOd(availableOD);
        accountRepository.save(account);
        return true;
    }
}