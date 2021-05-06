package ru.nsu.trivia.common.dto.model.task;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        include = JsonTypeInfo.As.PROPERTY,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = SelectAnswerAnswer.class, name = "selectAnswer"),
        @JsonSubTypes.Type(value = SetNearestValueAnswer.class, name = "setAnswer")
})
abstract public class Answer extends UsingTokenRequest {
    int round;

    public Answer() {
    }

    public Answer(String token, int round) {
        super(token);
        this.round = round;
    }

    public Answer(String token) {
        super(token);
    }

    public int getRound() {
        return round;
    }

    public void setRound(int round) {
        this.round = round;
    }
}
