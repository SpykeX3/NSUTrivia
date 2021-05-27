package ru.nsu.trivia.server.task;

import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO;
import ru.nsu.trivia.common.dto.model.task.SetNearestValueAnswer;
import ru.nsu.trivia.common.dto.model.task.SetNearestValueTaskDTO;

public class ScoringCalculator {
    public int calculateScore(SelectAnswerTaskDTO task, SelectAnswerAnswer answer) {
        return task.getCorrectVariantId() == answer.getVariantId() ? 100 : 0;
    }

    public int calculateScore(SetNearestValueTaskDTO task, SetNearestValueAnswer answer) {
        return (int) (200 * (1 - getLoss(task.getCorrectAnswer(), answer.getAnswer())));
    }

    private double getLoss(int correct, int current) {
        return Math.abs(Math.atan((correct - current) / (correct / 3.0)) / (Math.PI / 2));
    }
}
