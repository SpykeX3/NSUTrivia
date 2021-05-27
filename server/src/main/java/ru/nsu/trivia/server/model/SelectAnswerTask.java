package ru.nsu.trivia.server.model;

import java.util.List;

import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;

@Entity
public class SelectAnswerTask {
    @Id
    private long id;
    private String question;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> variants;
    private int correct;
    private int timeLimit;

    public SelectAnswerTask() {
    }

    public SelectAnswerTask(long id, String question, List<String> variants, int correct, int timeLimit) {
        this.id = id;
        this.question = question;
        this.variants = variants;
        this.correct = correct;
        this.timeLimit = timeLimit;
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

    public List<String> getVariants() {
        return variants;
    }

    public void setVariants(List<String> variants) {
        this.variants = variants;
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
