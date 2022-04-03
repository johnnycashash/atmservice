package com.zw.atmservice.card.controller;

import com.zw.atmservice.card.dto.CardDetail;
import com.zw.atmservice.card.exception.CardGeneralException;
import com.zw.atmservice.card.service.CardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping("/api/card")
@RestController
public class CardController {
    @Autowired
    private CardService cardService;

    @GetMapping("/checkCard/{id}")
    public ResponseEntity<CardDetail> checkCard(@PathVariable("id") Long id) throws CardGeneralException {
        return ResponseEntity.ok(cardService.findCardDetailByCardNumber(id));
    }

}
