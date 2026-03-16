package com.example.wallet_service.dtos.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IdempotencyRequestDto {
    @NotBlank(message = "Idempotency-Key is required")
    @Size(max = 50, message = "Idempotency-Key cannot exceed 50 characters")
    private String idempotencyKey;
}
