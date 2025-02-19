package com.sparta.delivery.config.global.exception.custom;

public class DeliveryAddressNotFoundException extends RuntimeException {
    public DeliveryAddressNotFoundException(String message) {
        super(message);
    }
}
