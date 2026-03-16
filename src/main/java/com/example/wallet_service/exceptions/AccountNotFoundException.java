package com.example.wallet_service.exceptions;


public class AccountNotFoundException extends RuntimeException {
    public AccountNotFoundException(String message){
        super(message);
    }

    public AccountNotFoundException(){
        super("Invalid account");
    }
}
