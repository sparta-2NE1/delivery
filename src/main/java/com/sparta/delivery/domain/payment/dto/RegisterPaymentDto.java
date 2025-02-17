package com.sparta.delivery.domain.payment.dto;

import lombok.*;

import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter

public class RegisterPaymentDto {
    private UUID cardId;
    private int amount;
    private UUID orderId;
}




