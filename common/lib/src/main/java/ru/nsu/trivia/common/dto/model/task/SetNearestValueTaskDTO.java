package ru.nsu.trivia.common.dto.model.task;

public class SetNearestValueTaskDTO extends TaskDTO {
    private String question;
    private Integer correctAnswer;

    public SetNearestValueTaskDTO() {
    }

    public SetNearestValueTaskDTO(String question, int correctAnswer) {
        this.question = question;
        this.correctAnswer = correctAnswer;
        this.type = Type.Type_answer;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public Integer getCorrectAnswer() {
        return correctAnswer;
    }

    public void setCorrectAnswer(Integer correctAnswer) {
        this.correctAnswer = correctAnswer;
    }
}
