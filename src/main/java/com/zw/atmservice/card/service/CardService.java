package com.zw.atmservice.card.service;

import com.zw.atmservice.card.dto.CardDetail;
import com.zw.atmservice.card.exception.CardGeneralException;

public interface CardService {
    CardDetail findCardDetailByCardNumber(Long cardNumber) throws CardGeneralException;
}
