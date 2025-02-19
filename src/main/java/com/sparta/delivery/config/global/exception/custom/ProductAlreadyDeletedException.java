package com.sparta.delivery.config.global.exception.custom;

public class ProductAlreadyDeletedException extends RuntimeException {
    public ProductAlreadyDeletedException(String message) {
        super(message);
    }
}
