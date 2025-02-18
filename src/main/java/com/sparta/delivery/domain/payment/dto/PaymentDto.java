package com.sparta.delivery.domain.payment.dto;

import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class PaymentDto {
    private UUID paymentId;
    private int amount;
    private UUID orderId;
    private LocalDateTime orderTime;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private String requirements;
}
