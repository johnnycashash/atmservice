package com.zw.atmservice.exception;

public class InvalidPinException extends Exception {
    public InvalidPinException() {
        super();
    }

    public InvalidPinException(String message) {
        super(message);
    }
}
