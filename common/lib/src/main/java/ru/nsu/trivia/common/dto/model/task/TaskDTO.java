package ru.nsu.trivia.common.dto.model.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectAnswerTaskDTO.class, name = "select"),
        @JsonSubTypes.Type(value = SetNearestValueTaskDTO.class, name = "set")
})
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
