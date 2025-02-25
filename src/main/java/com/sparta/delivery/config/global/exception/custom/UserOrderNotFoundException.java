package com.sparta.delivery.config.global.exception.custom;

public class UserOrderNotFoundException extends RuntimeException{
    public UserOrderNotFoundException(String message) { super(message);}
}
