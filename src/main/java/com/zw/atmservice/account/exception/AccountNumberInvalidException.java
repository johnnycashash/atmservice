package com.zw.atmservice.account.exception;

public class AccountNumberInvalidException extends RuntimeException {
    public AccountNumberInvalidException(String message) {
        super(message);
    }
}
