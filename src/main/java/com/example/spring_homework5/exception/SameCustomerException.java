package com.example.spring_homework5.exception;

public class SameCustomerException extends RuntimeException {
    public SameCustomerException(String message) {
        super(message);
    }
}