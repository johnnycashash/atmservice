package com.zw.atmservice.service.impl;

import com.zw.atmservice.dao.CardRepository;
import com.zw.atmservice.dto.CardDetail;
import com.zw.atmservice.entity.Card;
import com.zw.atmservice.exception.CardGeneralException;
import com.zw.atmservice.exception.CardNumberInvalidException;
import com.zw.atmservice.service.CardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepository cardRepository;

    @Override
    public CardDetail findCardDetailByCardNumber(Long cardNumber) throws CardNumberInvalidException, CardGeneralException {
        Optional<Card> card = cardRepository.findById(cardNumber);
        if (card.isPresent()) {
            CardDetail target = new CardDetail();
            BeanUtils.copyProperties(card.get(), target);
            return target;
        } else {
            throw new CardNumberInvalidException("Card number not found");
        }
    }
}
