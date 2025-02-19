package com.sparta.delivery.domain.order.enums;

public enum OrderStatus {
    //결제 대기중, 결제 완료, 주문 진행중, 배달중, 배달완료/픽업완료, 주문 취소
    PAYMENT_WAIT, PAYMENT_COMPLETE, ORDER_IN, DELIVERING, ORDER_COMPLETE, ORDER_CANCEL
}
