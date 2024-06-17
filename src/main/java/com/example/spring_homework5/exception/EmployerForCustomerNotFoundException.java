package com.example.spring_homework5.exception;

public class EmployerForCustomerNotFoundException extends RuntimeException {
    public EmployerForCustomerNotFoundException(String message) {
        super(message);
    }
}