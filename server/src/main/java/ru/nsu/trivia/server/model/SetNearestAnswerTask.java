package ru.nsu.trivia.server.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SetNearestAnswerTask {
    @Id
    private long id;
    private String question;
    private int correct;
    private int timeLimit;

    public SetNearestAnswerTask() {
    }

    public SetNearestAnswerTask(long id, String question, int correct) {
        this.id = id;
        this.question = question;
        this.correct = correct;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public int getCorrect() {
        return correct;
    }

    public void setCorrect(int correct) {
        this.correct = correct;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public void setTimeLimit(int timeLimit) {
        this.timeLimit = timeLimit;
    }
}
