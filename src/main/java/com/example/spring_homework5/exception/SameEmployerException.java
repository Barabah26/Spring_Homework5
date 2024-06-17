package com.example.spring_homework5.exception;

public class SameEmployerException extends RuntimeException {
    public SameEmployerException(String message) {
        super(message);
    }
}
