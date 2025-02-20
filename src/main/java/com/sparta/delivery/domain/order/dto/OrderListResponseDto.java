package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@AllArgsConstructor
public class OrderListResponseDto {
    private UUID orderId;
    private LocalDateTime orderTime;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private String requirements;
    private UUID stores;
    private UUID user;
    private UUID deliveryAddressId;
}
