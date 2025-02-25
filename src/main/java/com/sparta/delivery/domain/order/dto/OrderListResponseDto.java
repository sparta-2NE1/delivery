package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.review.dto.ReviewResponseDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
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
