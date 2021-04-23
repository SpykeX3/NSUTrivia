package ru.nsu.trivia.common.dto.model;

public class PlayerInLobby {
    String username;
    boolean isHost;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public boolean isHost() {
        return isHost;
    }

    public void setHost(boolean host) {
        isHost = host;
    }
}
