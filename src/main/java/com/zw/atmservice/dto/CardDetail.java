package com.zw.atmservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CardDetail {
    Long cardNumber;
    Long accountNumber;
    String hashedPin;
    boolean active;
}
