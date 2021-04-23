package ru.nsu.trivia.common.dto.model.task;

import java.util.List;

public class SelectAnswerTask extends Task {
    private String question;
    private List<String> variants;

    public SelectAnswerTask() {
    }

    public SelectAnswerTask(String question, List<String> variants) {
        this.question = question;
        this.variants = variants;
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
}
