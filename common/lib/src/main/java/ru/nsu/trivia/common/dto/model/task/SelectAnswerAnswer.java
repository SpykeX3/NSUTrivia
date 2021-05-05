package ru.nsu.trivia.common.dto.model.task;

public class SelectAnswerAnswer extends Answer {
    private int variantId;

    public SelectAnswerAnswer() {
        super();
    }

    public SelectAnswerAnswer(String token, int variantId) {
        super(token);
        this.variantId = variantId;
    }

    public int getVariantId() {
        return variantId;
    }

    public void setVariantId(int variantId) {
        this.variantId = variantId;
    }
}
