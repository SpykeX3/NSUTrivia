package ru.nsu.trivia.server.task;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.nsu.trivia.server.model.SelectAnswerTask;

public interface SelectAnswerTaskRepository extends JpaRepository<SelectAnswerTask, Long> {
}
