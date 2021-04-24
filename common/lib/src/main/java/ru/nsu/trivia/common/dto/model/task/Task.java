package ru.nsu.trivia.common.dto.model.task;

abstract public class Task {
    public enum Type{
        Select_answer,
        Type_answer
    }
    public Type type;
    public int timeLimit;

    public Task(Type type, int timeLimit) {
        this.type = type;
        this.timeLimit = timeLimit;
    }

    public Task() {
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
