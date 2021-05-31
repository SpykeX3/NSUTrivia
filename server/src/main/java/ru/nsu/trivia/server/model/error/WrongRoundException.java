package ru.nsu.trivia.server.model.error;

public class WrongRoundException extends RuntimeException{
    public WrongRoundException(String message) {
        super(message);
    }
}
