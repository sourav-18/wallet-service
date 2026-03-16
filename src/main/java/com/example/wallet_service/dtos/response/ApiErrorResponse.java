package com.example.wallet_service.dtos.response;

public class ApiErrorResponse extends BaseApiResponse{
    public ApiErrorResponse(Integer status, String message) {
        super(false, status, message);
    }
}
