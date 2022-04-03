package com.zw.atmservice.account.service.impl;

import com.zw.atmservice.account.dao.AccountRepository;
import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.entity.Account;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.account.exception.AccountNumberInvalidException;
import com.zw.atmservice.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Slf4j
@Service
public class AccountServiceImpl implements AccountService {
    @Autowired
    private AccountRepository accountRepository;

    @Override
    public AccountDetail findAccountDetailByAccountNumber(Long accountNumber) throws AccountGeneralException {
        try {
            Optional<Account> account = accountRepository.findById(accountNumber);
            if (account.isPresent()) {
                AccountDetail target = new AccountDetail();
                BeanUtils.copyProperties(account.get(), target);
                return target;
            } else {
                throw new AccountNumberInvalidException("Account number not found");
            }
        } catch (Exception exception) {
            throw new AccountGeneralException("Account Internal Server Error");
        }

    }

    @Override
    public void updateAccountBalance(AccountDetail acccountDetail, long availableBalance, long availableOD) throws AccountGeneralException {
        try {
            Account account = new Account();
            BeanUtils.copyProperties(acccountDetail, account);
            account.setBalance(availableBalance);
            account.setOd(availableOD);
            accountRepository.save(account);
        } catch (Exception exception) {
            log.error("Error", exception);
            throw new AccountGeneralException("Account Internal Server Error");
        }
    }
}