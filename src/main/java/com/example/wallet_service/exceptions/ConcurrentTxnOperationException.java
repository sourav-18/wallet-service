package com.example.wallet_service.exceptions;

public class ConcurrentTxnOperationException extends RuntimeException {
    public ConcurrentTxnOperationException(String message) {
        super(message);
    }
}
