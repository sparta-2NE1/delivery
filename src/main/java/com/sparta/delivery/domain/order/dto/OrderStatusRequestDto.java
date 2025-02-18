package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.enums.OrderStatus;
import lombok.Getter;

@Getter
public class OrderStatusRequestDto {
    private OrderStatus updateStatus;
}
