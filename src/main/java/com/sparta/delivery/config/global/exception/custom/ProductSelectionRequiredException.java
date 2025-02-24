package com.sparta.delivery.config.global.exception.custom;

public class ProductSelectionRequiredException extends RuntimeException {
    public ProductSelectionRequiredException(String message) { super(message);
    }
}
