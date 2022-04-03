package com.zw.atmservice.atm.exception;

public class InactiveCardException extends Exception {
    public InactiveCardException(String message) {
        super(message);
    }
}
