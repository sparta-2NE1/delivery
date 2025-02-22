package com.sparta.delivery.config.global.exception.custom;

public class InvalidApiResponseException extends RuntimeException {
    public InvalidApiResponseException(String message) {
        super(message);
    }
}
