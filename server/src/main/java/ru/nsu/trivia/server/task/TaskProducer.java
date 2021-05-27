package ru.nsu.trivia.server.task;

import ru.nsu.trivia.common.dto.model.task.TaskDTO;

@FunctionalInterface
public interface TaskProducer {
    TaskDTO produce();
}
