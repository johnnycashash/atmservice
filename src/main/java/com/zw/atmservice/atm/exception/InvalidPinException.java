package com.zw.atmservice.atm.exception;

public class InvalidPinException extends Exception {
    public InvalidPinException() {
        super();
    }

    public InvalidPinException(String message) {
        super(message);
    }
}
