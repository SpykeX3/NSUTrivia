package ru.nsu.trivia.server.model.error;

public class UnknownTokenException extends RuntimeException {
    public UnknownTokenException(String message) {
        super(message);
    }
}
