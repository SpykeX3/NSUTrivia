package ru.nsu.trivia.server.model.error;

public class PlayerAlreadyInLobbyException extends RuntimeException {
    public PlayerAlreadyInLobbyException(String message) {
        super(message);
    }
}
