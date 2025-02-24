package com.sparta.delivery.config.global.exception.custom;

public class OrderNotFoundException extends RuntimeException {
    public OrderNotFoundException(String message) { super(message);
    }
}
