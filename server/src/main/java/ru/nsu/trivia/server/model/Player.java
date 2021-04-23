package ru.nsu.trivia.server.model;

import ru.nsu.trivia.server.sessions.SessionData;

public class Player extends SessionData {
    private int score;
    boolean ready;

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }
}
