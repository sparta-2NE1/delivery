package com.sparta.delivery.config.global.exception.custom;

public class PaymentAlreadyCompletedException extends RuntimeException {
    public PaymentAlreadyCompletedException(String message) {
        super(message);
    }
}