package com.sparta.delivery.config.global.exception.custom;

public class ReviewNotAllowedException extends RuntimeException {
    public ReviewNotAllowedException(String message) { super(message);
    }
}
