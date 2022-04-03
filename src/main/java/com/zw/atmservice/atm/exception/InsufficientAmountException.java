package com.zw.atmservice.atm.exception;

public class InsufficientAmountException extends Exception {
    public InsufficientAmountException(String message) {
        super(message);
    }
}
