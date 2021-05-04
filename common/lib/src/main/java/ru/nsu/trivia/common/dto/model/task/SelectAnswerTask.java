package ru.nsu.trivia.common.dto.model.task;

import java.util.List;

public class SelectAnswerTask extends Task {
    private String question;
    private List<String> variants;
    private Integer correctVariantId;

    public SelectAnswerTask() {
    }

    public SelectAnswerTask(String question, List<String> variants, int correctVariantId) {
        this.question = question;
        this.variants = variants;
        this.correctVariantId = correctVariantId;
        this.type = Type.Select_answer;
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

    public Integer getCorrectVariantId() {
        return correctVariantId;
    }

    public void setCorrectVariantId(Integer correctVariantId) {
        this.correctVariantId = correctVariantId;
    }
}
