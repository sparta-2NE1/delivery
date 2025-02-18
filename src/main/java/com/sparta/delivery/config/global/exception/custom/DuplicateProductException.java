package com.sparta.delivery.config.global.exception.custom;

public class DuplicateProductException extends RuntimeException {
    public DuplicateProductException(String message) {
        super(message);
    }
}
