package ru.nsu.trivia.server.model;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import ru.nsu.trivia.common.dto.model.LobbyState;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;

@ParametersAreNonnullByDefault
public class Lobby implements Comparable<Lobby> {

    private LobbyState state;
    private String id;
    private List<Player> players;
    private int round;
    private long lastUpdate;
    private TaskDTO currentTask;
    private long taskDeadline;
    private final long creationTime;
    private List<TaskDTO> previousTasks;

    public Lobby() {
        state = LobbyState.Waiting;
        round = 0;
        currentTask = null;
        players = new ArrayList<>(2);
        creationTime = System.currentTimeMillis();
        previousTasks = new ArrayList<>();
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

    public TaskDTO getCurrentTask() {
        return currentTask;
    }

    public void setCurrentTask(TaskDTO currentTaskDTO) {
        this.currentTask = currentTaskDTO;
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

    public List<TaskDTO> getPreviousTasks() {
        return previousTasks;
    }

    public void setPreviousTasks(List<TaskDTO> previousTaskDTOS) {
        this.previousTasks = previousTaskDTOS;
    }

    public void addPreviousTask(TaskDTO taskDTO) {
        previousTasks.add(taskDTO);
    }

    public void setNewTask(TaskDTO taskDTO) {
        round++;
        if (currentTask != null) {
            addPreviousTask(currentTask);
        }
        setCurrentTask(taskDTO);
        taskDeadline = System.currentTimeMillis() + currentTask.getTimeLimit();
    }

    public long getTaskDeadline() {
        return taskDeadline;
    }

    public void setTaskDeadline(long taskDeadline) {
        this.taskDeadline = taskDeadline;
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Lobby && id.equals(((Lobby) obj).id);
    }

    @Override
    public int compareTo(Lobby o) {
        return Long.compare(taskDeadline, o.taskDeadline);
    }
}
