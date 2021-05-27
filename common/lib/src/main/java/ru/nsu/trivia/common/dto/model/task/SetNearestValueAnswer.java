package ru.nsu.trivia.common.dto.model.task;

public class SetNearestValueAnswer extends Answer {
    private int answer;

    public SetNearestValueAnswer() {
    }

    public SetNearestValueAnswer(String token, int round, int answer) {
        super(token, round);
        this.answer = answer;
    }

    public int getAnswer() {
        return answer;
    }

    public void setAnswer(int answer) {
        this.answer = answer;
    }
}
