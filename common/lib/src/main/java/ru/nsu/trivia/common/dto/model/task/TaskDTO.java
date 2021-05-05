package ru.nsu.trivia.common.dto.model.task;

abstract public class TaskDTO {
    public enum Type {
        Select_answer,
        Type_answer
    }

    public Type type;
    public int timeLimit;

    public TaskDTO(Type type, int timeLimit) {
        this.type = type;
        this.timeLimit = timeLimit;
    }

    public TaskDTO() {
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
