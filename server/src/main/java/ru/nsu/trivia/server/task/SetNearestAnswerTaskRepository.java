package ru.nsu.trivia.server.task;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.nsu.trivia.server.model.SelectAnswerTask;
import ru.nsu.trivia.server.model.SetNearestAnswerTask;

public interface SetNearestAnswerTaskRepository extends JpaRepository<SetNearestAnswerTask, Long> {
}
