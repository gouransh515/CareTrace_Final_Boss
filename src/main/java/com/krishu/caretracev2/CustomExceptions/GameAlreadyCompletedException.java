package com.krishu.caretracev2.CustomExceptions;

public class GameAlreadyCompletedException extends RuntimeException {
    public GameAlreadyCompletedException(String message) {
        super(message);
    }
}
