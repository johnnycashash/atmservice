package com.zw.atmservice.controller;

import com.zw.atmservice.dto.BalanceCheckRequest;
import com.zw.atmservice.dto.BalanceCheckResponse;
import com.zw.atmservice.dto.WithdrawBalanceRequest;
import com.zw.atmservice.dto.WithdrawBalanceResponse;
import com.zw.atmservice.exception.InactiveAccountException;
import com.zw.atmservice.exception.InactiveCardException;
import com.zw.atmservice.exception.InvalidPinException;
import com.zw.atmservice.service.ATMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;

@Slf4j
@RequestMapping("/api/atm")
@RestController
public class ATMController {

    @Autowired
    private ATMService atmService;

    @PostMapping("/checkBalance")
    public ResponseEntity<BalanceCheckResponse> getBalance(@RequestBody @Valid BalanceCheckRequest balanceCheckRequest)
            throws InactiveCardException, InvalidPinException, InactiveAccountException {
        return ResponseEntity.ok(atmService.getBalance(balanceCheckRequest));
    }

    @PostMapping("/withdrawBalance")
    public ResponseEntity<WithdrawBalanceResponse> withdrawBalance(@RequestBody @Valid WithdrawBalanceRequest withdrawBalanceRequest)
            throws InactiveCardException, InvalidPinException, InactiveAccountException {
        return ResponseEntity.ok(atmService.withdrawBalance(withdrawBalanceRequest));
    }
}
