package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.enums.OrderType;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
public class OrderRequestDto {
    private Long user_id;
    private UUID delivery_address_id;
    private UUID store_id;
    private List<UUID> product_id;
    private OrderType orderType;
    private String requirements;
}
