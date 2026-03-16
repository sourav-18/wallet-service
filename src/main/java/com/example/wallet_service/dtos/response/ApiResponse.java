package com.example.wallet_service.dtos.response;

import lombok.Getter;

@Getter
public class ApiResponse<T> extends BaseApiResponse{
    private final T data;

    public ApiResponse(Integer status, String message, T data) {
        super(true, status, message);
        this.data=data;
    }
}
