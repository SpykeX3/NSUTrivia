package ru.nsu.trivia.common.dto.model;

import java.util.List;

import ru.nsu.trivia.common.dto.model.task.Task;

public class LobbyDTO {

    private List<PlayerInLobby> players;
    private int round;
    private LobbyState state;
    private String id;
    private Task currentTask;
    private long lastUpdated;

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public long getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(long lastUpdated) {
        this.lastUpdated = lastUpdated;
    }
}
