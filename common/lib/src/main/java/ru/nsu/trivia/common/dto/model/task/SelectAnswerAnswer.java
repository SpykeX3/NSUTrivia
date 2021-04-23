package ru.nsu.trivia.common.dto.model.task;

public class SelectAnswerAnswer {
    private int variantId;

    public SelectAnswerAnswer() {
    }

    public SelectAnswerAnswer(int variantId) {
        this.variantId = variantId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }
}
