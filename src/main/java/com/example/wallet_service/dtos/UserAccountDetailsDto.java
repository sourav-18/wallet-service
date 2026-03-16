package com.example.wallet_service.dtos;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class UserAccountDetailsDto {
    private Long id;
    private Long assetId;
    private Double amount;

    public UserAccountDetailsDto(Long id, Long assetId, Double amount) {
        this.id = id;
        this.assetId = assetId;
        this.amount = amount;
    }
}
