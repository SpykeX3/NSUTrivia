package ru.nsu.trivia.common.dto.model.task;

import java.util.List;
import java.util.Objects;

public class SelectAnswerTaskDTO extends TaskDTO {
    private String question;
    private List<String> variants;
    private Integer correctVariantId;

    public SelectAnswerTaskDTO() {
    }

    public SelectAnswerTaskDTO(String question, List<String> variants, int correctVariantId) {
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

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        SelectAnswerTaskDTO that = (SelectAnswerTaskDTO) o;
        return question.equals(that.question);
    }

    @Override
    public int hashCode() {
        return Objects.hash(question);
    }
}
