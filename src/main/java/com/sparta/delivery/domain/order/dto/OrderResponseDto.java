package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@AllArgsConstructor
public class OrderResponseDto {
    private UUID orderId;
    private LocalDateTime orderTime;
    private OrderType orderType;
    private OrderStatus orderStatus;
    private String requirements;
    private Stores stores;
    private User user;
}
