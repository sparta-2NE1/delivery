package com.sparta.delivery.config.global.exception.custom;

public class ProductQuantityNotAllowedException extends RuntimeException{
    public ProductQuantityNotAllowedException(String message) {super(message);}
}
