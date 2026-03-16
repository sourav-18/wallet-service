package com.example.wallet_service.controllers;


import com.example.wallet_service.dtos.SystemAccountDetailsDto;
import com.example.wallet_service.dtos.UserAccountDetailsDto;
import com.example.wallet_service.dtos.request.BonusRequestDto;

import com.example.wallet_service.dtos.request.PurchaseRequestDto;
import com.example.wallet_service.dtos.request.TopupRequestDto;
import com.example.wallet_service.dtos.response.ApiResponse;
import com.example.wallet_service.services.WalletService;
import jakarta.validation.Valid;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/wallet")
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;
    private final String idempotencyKeyHeader="Idempotency-Key";
    private static final long systemTreasuryAccount = 2;
    private static final long systemRevenueAccount = 1;

    @PostMapping("/topup")
    public ResponseEntity<ApiResponse<Void>> topup(
            @RequestHeader(idempotencyKeyHeader) String idempotencyKey,
            @Valid @RequestBody TopupRequestDto topupRequestDto) {
        ApiResponse<Void> apiResponse=walletService.topup(topupRequestDto, idempotencyKey);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/bonus")
    public ResponseEntity<ApiResponse<Void>> bonus(
            @RequestHeader(idempotencyKeyHeader) String idempotencyKey,
            @Valid @RequestBody BonusRequestDto requestDto) {
        ApiResponse<Void> apiResponse=walletService.bonus(requestDto, idempotencyKey);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @PostMapping("/purchase")
    public ResponseEntity<ApiResponse<Void>> purchase(
            @RequestHeader(idempotencyKeyHeader) String idempotencyKey,
            @Valid @RequestBody PurchaseRequestDto requestDto) {
        ApiResponse<Void> apiResponse=walletService.purchase(requestDto, idempotencyKey);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UserAccountDetailsDto>>> getBalance(@RequestParam(value = "userId") Long userId){
        ApiResponse<List<UserAccountDetailsDto>> apiResponse=walletService.getUserBalance(userId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/assets/{assetId}")
    public ResponseEntity<ApiResponse<UserAccountDetailsDto>> getBalanceByAssetId(
            @PathVariable("assetId") Long assetId,
            @RequestParam(value = "userId") Long userId){
        ApiResponse<UserAccountDetailsDto> apiResponse=walletService.getUserBalanceByAssetId(userId,assetId);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/system/revenue")
    public ResponseEntity<ApiResponse<SystemAccountDetailsDto>> getSystemRevenueBalance(){
        ApiResponse<SystemAccountDetailsDto> apiResponse=walletService.getSystemBalanceById(systemRevenueAccount);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }

    @GetMapping("/system/treasury")
    public ResponseEntity<ApiResponse<SystemAccountDetailsDto>> getSystemTreasuryBalance(){
        ApiResponse<SystemAccountDetailsDto> apiResponse=walletService.getSystemBalanceById(systemTreasuryAccount);
        return ResponseEntity.status(apiResponse.getStatus()).body(apiResponse);
    }


}
