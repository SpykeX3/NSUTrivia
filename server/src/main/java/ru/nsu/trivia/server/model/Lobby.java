package ru.nsu.trivia.server.model;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.trivia.common.dto.model.LobbyState;
import ru.nsu.trivia.common.dto.model.task.Task;

public class Lobby {

    private LobbyState state;
    private String id;
    private List<Player> players;
    private int round;
    private long lastUpdate;
    private Task currentTask;
    private final long creationTime;

    public Lobby() {
        state = LobbyState.Waiting;
        round = 0;
        currentTask = null;
        players = new ArrayList<>(2);
        creationTime = System.currentTimeMillis();
    }


    public LobbyState getState() {
        return state;
    }

    public void setState(LobbyState state) {
        this.state = state;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void addPlayer(Player player) {
        players.add(player);
    }

    public void removePlayer(Player player) {
        players.remove(player);
    }

    public int getPlayerCount() {
        return players.size();
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }

    public Task getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(Task currentTask) {
        this.currentTask = currentTask;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCreationTime() {
        return creationTime;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lobby && id.equals(((Lobby) obj).id);
    }
}
