package ru.nsu.trivia.server.model.converters;

import ru.nsu.trivia.common.dto.model.task.SetNearestValueTaskDTO;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.SetNearestAnswerTask;

public class SetNearestAnswerTaskConverter {
    public static TaskDTO convert(SetNearestAnswerTask task) {
        SetNearestValueTaskDTO dto = new SetNearestValueTaskDTO();
        dto.setQuestion(task.getQuestion());
        dto.setTimeLimit(task.getTimeLimit() * 1000);
        dto.setCorrectAnswer(task.getCorrect());
        dto.setType(TaskDTO.Type.Type_answer);
        return dto;
    }
}
