package com.example.wallet_service.exceptions;

public class AccountCreditFailedException extends RuntimeException {
    public AccountCreditFailedException(String message) {
        super(message);
    }
}
