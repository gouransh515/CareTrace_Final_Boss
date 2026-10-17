package com.krishu.caretracev2.CustomExceptions;

public class GameNotCompleteException extends RuntimeException {
    public GameNotCompleteException(String message) {
        super(message);
    }
}
