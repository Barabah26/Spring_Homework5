package com.example.spring_homework5.exception;

public class CustomerForEmployerNotFoundException extends RuntimeException {
    public CustomerForEmployerNotFoundException(String message) {
        super(message);
    }
}