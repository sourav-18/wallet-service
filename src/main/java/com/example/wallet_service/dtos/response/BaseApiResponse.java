package com.example.wallet_service.dtos.response;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class BaseApiResponse {
    private final Boolean success;
    private final Integer status;
    private final String message;
    private final LocalDateTime timestamp=LocalDateTime.now();

    public BaseApiResponse(Boolean success, Integer status, String message) {
        this.success = success;
        this.status = status;
        this.message = message;
    }
}
