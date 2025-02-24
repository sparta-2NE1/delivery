package com.sparta.delivery.config.global.exception.custom;

public class OrderModificationNotAllowedException extends RuntimeException {
    public OrderModificationNotAllowedException(String message) { super(message);}
}
