package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.delivery_address.entity.DeliveryAddress;
import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
public class OrderRequestDto {
    private UUID deliveryAddressId;
    private UUID storeId;
    private List<UUID> productId;
    private OrderType orderType;
    private String requirements;

    public Order createOrder(Stores store, DeliveryAddress deliveryAddress, User user) {
        return Order.builder()
                .orderTime(LocalDateTime.now())
                .orderType(orderType)
                //주문 생성 시 기본 상태는 주문 확인
                .orderStatus(OrderStatus.ORDER_IN)
                .requirements(requirements)
                .stores(store)
                .deliveryAddress(deliveryAddress)
                .user(user).build();
    }
}
