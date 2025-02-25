package com.sparta.delivery.config.global.exception.custom;

public class ReviewAlreadyExistsException extends RuntimeException {
    public ReviewAlreadyExistsException(String message) { super(message);
    }
}
