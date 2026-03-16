package com.example.wallet_service.dtos.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.validator.constraints.Range;


@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BonusRequestDto {

    @NotNull(message = "userId can't be null")
    @Min(value = 1, message = "userId can't be less then 1")
    private Long userId;

    @Range(min = 1 , max = 3 ,message = "assetId should be 1 or 2 or 3")
    private Long assetId;

    @NotNull(message = "amount can't be null")
    @Min(value = 1, message = "amount can't be less then 1")
    private Double amount;
}
