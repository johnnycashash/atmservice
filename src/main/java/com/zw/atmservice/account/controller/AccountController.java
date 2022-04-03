package com.zw.atmservice.account.controller;

import com.zw.atmservice.account.dto.AccountDetail;
import com.zw.atmservice.account.exception.AccountGeneralException;
import com.zw.atmservice.account.service.AccountService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/account")
@RestController
public class AccountController {
    @Autowired
    private AccountService accountService;

    @GetMapping("/checkAccount/{id}")
    public ResponseEntity<AccountDetail> checkAccount(@PathVariable("id") Long id) throws AccountGeneralException {
        return ResponseEntity.ok(accountService.findAccountDetailByAccountNumber(id));
    }

}
