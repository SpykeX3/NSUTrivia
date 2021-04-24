package ru.nsu.trivia.common.dto.model;

import java.util.List;

public class LobbyDTO {
    public enum LobbyState {
        Waiting,
        Playing,
        Closed
    }

    private List<PlayerInLobby> players;
    private int round;
    private LobbyState state;

    public LobbyDTO() {
    }

    public LobbyDTO(List<PlayerInLobby> players, int round, LobbyState state) {
        this.players = players;
        this.round = round;
        this.state = state;
    }

    public List<PlayerInLobby> getPlayers() {
        return players;
    }

    public void setPlayers(List<PlayerInLobby> players) {
        this.players = players;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public LobbyState getState() {
        return state;
    }

    public void setState(LobbyState state) {
        this.state = state;
    }
}
