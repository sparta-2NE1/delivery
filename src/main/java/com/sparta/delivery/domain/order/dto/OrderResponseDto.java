package com.sparta.delivery.domain.order.dto;

import com.sparta.delivery.domain.order.entity.Order;
import com.sparta.delivery.domain.order.enums.OrderStatus;
import com.sparta.delivery.domain.order.enums.OrderType;
import com.sparta.delivery.domain.payment.entity.Payment;
import com.sparta.delivery.domain.store.entity.Stores;
import com.sparta.delivery.domain.user.entity.User;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

public class OrderResponseDto {
    private String order_id;
    private LocalDateTime order_time;
    private OrderType order_type;
    private OrderStatus order_status;
    private String requirements;
    private Stores stores;
    private Payment payment;
    private User user;

    public OrderResponseDto(Order order) {
        this.order_id = order.getOrderId().toString();
        this.order_time = order.getOrderTime();
        this.order_type = order.getOrderType();
        this.order_status = order.getOrderStatus();
        this.requirements = order.getRequirements();
        this.stores = order.getStores();
        this.payment = order.getPayment();
        this.user = order.getUser();
    }
}
