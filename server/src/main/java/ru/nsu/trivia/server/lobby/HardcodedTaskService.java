package ru.nsu.trivia.server.lobby;

import java.util.List;


import ru.nsu.trivia.common.dto.model.task.Answer;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;

public class HardcodedTaskService implements TaskService {

    public TaskDTO generateTask() {
        var task = new SelectAnswerTaskDTO();
        task.setType(TaskDTO.Type.Select_answer);
        task.setQuestion("Select A");
        task.setVariants(List.of("A", "B", "C", "D"));
        task.setCorrectVariantId(0);
        task.setTimeLimit(60000);
        return task;
    }

    public int getScore(TaskDTO taskDTO, Answer answer) {
        return ((SelectAnswerTaskDTO) taskDTO).getCorrectVariantId() == ((SelectAnswerAnswer) answer).getVariantId()
                ? 100 : 0;
    }

}
