package com.sparta.delivery.config.global.exception.custom;

public class ForbiddenException extends RuntimeException{
    public ForbiddenException(String message){
        super(message);}
}
