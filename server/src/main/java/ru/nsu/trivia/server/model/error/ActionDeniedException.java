package ru.nsu.trivia.server.model.error;

public class ActionDeniedException extends RuntimeException {
    public ActionDeniedException(String message) {
        super(message);
    }
}
