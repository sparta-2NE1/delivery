package com.sparta.delivery.config.global.exception.custom;

public class ReviewNotFoundException extends RuntimeException {
    public ReviewNotFoundException(String message) { super(message);
    }
}
