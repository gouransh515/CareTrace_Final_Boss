package com.krishu.caretracev2.CustomExceptions;

public class OutOfRangeException extends RuntimeException {
    public OutOfRangeException(String message) {
        super(message);
    }
}
