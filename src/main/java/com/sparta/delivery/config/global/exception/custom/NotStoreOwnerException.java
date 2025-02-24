package com.sparta.delivery.config.global.exception.custom;

public class NotStoreOwnerException extends RuntimeException {
    public NotStoreOwnerException(String message) { super(message);
    }
}
