package ru.nsu.trivia.server.model.error;

public class InvalidRoomCodeException extends RuntimeException {
    public InvalidRoomCodeException(String message) {
        super(message);
    }
}
