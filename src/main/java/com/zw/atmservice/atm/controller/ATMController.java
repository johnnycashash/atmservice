package com.zw.atmservice.atm.controller;

import com.zw.atmservice.atm.dto.BalanceCheckRequest;
import com.zw.atmservice.atm.dto.BalanceCheckResponse;
import com.zw.atmservice.atm.dto.WithdrawBalanceRequest;
import com.zw.atmservice.atm.dto.WithdrawBalanceResponse;
import com.zw.atmservice.atm.exception.*;
import com.zw.atmservice.atm.service.ATMService;
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
            throws InactiveCardException, InvalidPinException, InactiveAccountException, ATMGeneralException {
        return ResponseEntity.ok(atmService.getBalance(balanceCheckRequest));
    }

    @PostMapping("/withdrawBalance")
    public ResponseEntity<WithdrawBalanceResponse> withdrawBalance(@RequestBody @Valid WithdrawBalanceRequest withdrawBalanceRequest)
            throws InactiveCardException, InvalidPinException, InactiveAccountException, AmountInDispensableException, InsufficientAmountException, ATMGeneralException {
        return ResponseEntity.ok(atmService.withdrawBalance(withdrawBalanceRequest));
    }
}
