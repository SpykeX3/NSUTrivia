package ru.nsu.trivia.server.lobby;

import ru.nsu.trivia.common.dto.model.task.Answer;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;

public interface TaskService {
    int getScore(TaskDTO task, Answer answer);

    TaskDTO generateTask();
}
