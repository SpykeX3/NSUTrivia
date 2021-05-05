package ru.nsu.trivia.common.dto.model.task;

import ru.nsu.trivia.common.dto.requests.UsingTokenRequest;

abstract public class Answer extends UsingTokenRequest {
    int round;

    public Answer() {
    }

    public Answer(String token, int round) {
        super(token);
        this.round = round;
    }

    public Answer(String token) {
        super(token);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
