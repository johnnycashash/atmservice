package com.zw.atmservice.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("card")
@Data
@AllArgsConstructor
public class Card {
    @Id
    Long cardNumber;
    Long accountNumber;
    String hashedPin;
    boolean active;
}
