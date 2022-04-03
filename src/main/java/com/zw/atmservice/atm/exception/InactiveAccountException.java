package com.zw.atmservice.atm.exception;

public class InactiveAccountException extends Exception {
    public InactiveAccountException(String message) {
        super(message);
    }
}
