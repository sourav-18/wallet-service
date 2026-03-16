package com.example.wallet_service.dtos;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SystemAccountDetailsDto {
    private Long id;
    private Double amount;

    public SystemAccountDetailsDto(Long id,Double amount) {
        this.id = id;
        this.amount = amount;
    }
}
