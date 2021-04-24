package ru.nsu.trivia.server.model;

import ru.nsu.trivia.server.sessions.SessionData;

public class Player extends SessionData {
    private int score = 0;
    boolean host = false;

    public Player() {
    }

    public Player(SessionData session) {
        setToken(session.getToken());
        setNickname(session.getNickname());
    }

    public Player(String token, String nickname, Long lobbyId, int score, boolean host) {
        super(token, nickname, lobbyId);
        this.score = score;
        this.host = host;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public boolean isHost() {
        return host;
    }

    public void setHost(boolean host) {
        this.host = host;
    }
}
