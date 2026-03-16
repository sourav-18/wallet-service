package com.example.wallet_service.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TransactionHistoryDto {
    Long userId;
    Long assetId;
    Long systemAccountId;
    Double amount;
    String reason;
}
