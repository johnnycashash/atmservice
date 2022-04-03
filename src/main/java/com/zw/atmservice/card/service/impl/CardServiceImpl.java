package com.zw.atmservice.card.service.impl;

import com.zw.atmservice.card.dao.CardRepository;
import com.zw.atmservice.card.dto.CardDetail;
import com.zw.atmservice.card.entity.Card;
import com.zw.atmservice.card.exception.CardGeneralException;
import com.zw.atmservice.card.exception.CardNumberInvalidException;
import com.zw.atmservice.card.service.CardService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {
    @Autowired
    private CardRepository cardRepository;

    @Override
    public CardDetail findCardDetailByCardNumber(Long cardNumber) throws CardGeneralException {
        try {
            Optional<Card> card = cardRepository.findById(cardNumber);
            if (card.isPresent()) {
                CardDetail target = new CardDetail();
                BeanUtils.copyProperties(card.get(), target);
                return target;
            } else {
                throw new CardNumberInvalidException("Card number not found");
            }
        } catch (Exception exception) {
            throw new CardGeneralException("Card Internal server error");
        }
    }
}
