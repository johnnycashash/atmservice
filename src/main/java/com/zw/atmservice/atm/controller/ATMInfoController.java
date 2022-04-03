package com.zw.atmservice.atm.controller;

import com.zw.atmservice.atm.dto.AtmInfoDetail;
import com.zw.atmservice.atm.exception.ATMGeneralException;
import com.zw.atmservice.atm.service.ATMService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/atminfo")
@RestController
public class ATMInfoController {
    @Autowired
    private ATMService atmService;

    @GetMapping("/checkAtm/{id}")
    public ResponseEntity<AtmInfoDetail> checkAtm(@PathVariable("id") Long id) throws ATMGeneralException {
        return ResponseEntity.ok(atmService.checkAtm(id));
    }
}
