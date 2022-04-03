package com.zw.atmservice.service;

import com.zw.atmservice.dto.CardDetail;
import com.zw.atmservice.exception.CardGeneralException;
import com.zw.atmservice.exception.CardNumberInvalidException;

public interface CardService {
    CardDetail findCardDetailByCardNumber(Long cardNumber) throws CardNumberInvalidException, CardGeneralException;
}
