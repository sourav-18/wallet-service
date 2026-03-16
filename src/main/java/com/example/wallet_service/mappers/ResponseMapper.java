package com.example.wallet_service.mappers;


import com.example.wallet_service.dtos.response.ApiResponse;

public class ResponseMapper {
    public static <T> ApiResponse<T> success(Integer status, String message, T data){
        return new ApiResponse<>(status,message,data);
    }

    public static ApiResponse<Void> success(Integer status,String message){
        return new ApiResponse<>(status,message,null);
    }

}
