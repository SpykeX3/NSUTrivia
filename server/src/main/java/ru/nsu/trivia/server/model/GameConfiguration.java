package ru.nsu.trivia.server.model;

import java.util.ArrayList;
import java.util.List;

import ru.nsu.trivia.common.dto.model.task.TaskDTO;

public class GameConfiguration {
    private List<TaskDTO.Type> taskTypes = new ArrayList<>();

    public GameConfiguration() {
    }

    public GameConfiguration(List<TaskDTO.Type> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public List<TaskDTO.Type> getTaskTypes() {
        return taskTypes;
    }

    public void setTaskTypes(List<TaskDTO.Type> taskTypes) {
        this.taskTypes = taskTypes;
    }

    public void addTask(TaskDTO.Type taskType) {
        this.taskTypes.add(taskType);
    }
}
