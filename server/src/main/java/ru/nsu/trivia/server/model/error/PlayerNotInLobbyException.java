package ru.nsu.trivia.server.model.error;

public class PlayerNotInLobbyException extends RuntimeException {
    public PlayerNotInLobbyException(String message) {
        super(message);
    }
}
