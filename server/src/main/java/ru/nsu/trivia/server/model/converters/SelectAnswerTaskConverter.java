package ru.nsu.trivia.server.model.converters;

import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.SelectAnswerTask;

public class SelectAnswerTaskConverter {
    public static TaskDTO convert(SelectAnswerTask selectAnswerTask) {
        SelectAnswerTaskDTO dto = new SelectAnswerTaskDTO();
        dto.setCorrectVariantId(selectAnswerTask.getCorrect());
        dto.setTimeLimit(selectAnswerTask.getTimeLimit());
        dto.setVariants(selectAnswerTask.getVariants());
        dto.setQuestion(selectAnswerTask.getQuestion());
        dto.setType(TaskDTO.Type.Select_answer);
        return dto;
    }
}
