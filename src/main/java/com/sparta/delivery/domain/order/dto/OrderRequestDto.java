package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.enums.OrderType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class OrderRequestDto {
    private String username;
    private UUID deliveryAddressId;
    private UUID storeId;
    private List<UUID> productId;
    private OrderType orderType;
    private String requirements;
}
