package ru.nsu.trivia.common.dto.model;

public class PlayerInLobby implements Comparable {
    String username;
    boolean isHost;
    int score;

    public PlayerInLobby() {
    }

    public PlayerInLobby(String username, boolean isHost) {
        this.username = username;
        this.isHost = isHost;
    }

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

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    @Override
    public int compareTo(Object o) {
        if (!(o instanceof PlayerInLobby))
            return 0;
       return -(score - ((PlayerInLobby) o).score);
    }
}
