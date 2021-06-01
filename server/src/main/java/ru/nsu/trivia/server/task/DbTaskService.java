package ru.nsu.trivia.server.task;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import ru.nsu.trivia.common.dto.model.task.Answer;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerAnswer;
import ru.nsu.trivia.common.dto.model.task.SelectAnswerTaskDTO;
import ru.nsu.trivia.common.dto.model.task.SetNearestValueAnswer;
import ru.nsu.trivia.common.dto.model.task.SetNearestValueTaskDTO;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.Lobby;

public class DbTaskService implements TaskService {
    ScoringCalculator scoringCalculator = new ScoringCalculator();
    Map<TaskDTO.Type, TaskProducer> producers;

    public DbTaskService(DbSelectAnswerTaskProducer dbSelectAnswerTaskProducer,
                         DbSetNearestAnswerTaskProducer dbSetNearestAnswerTaskProducer) {
        producers = new HashMap<>();
        producers.put(TaskDTO.Type.Select_answer, dbSelectAnswerTaskProducer);
        producers.put(TaskDTO.Type.Type_answer, dbSetNearestAnswerTaskProducer);
    }

    @Override
    public int getScore(TaskDTO task, Answer answer) {
        switch (task.type) {
            case Type_answer:
                return scoringCalculator.calculateScore((SetNearestValueTaskDTO) task, (SetNearestValueAnswer) answer);

            case Select_answer:
                return scoringCalculator.calculateScore((SelectAnswerTaskDTO) task, (SelectAnswerAnswer) answer);

            default:
                throw new RuntimeException("Unsupported task type " + task.type);
        }
    }

    @Override
    public TaskDTO generateTask(Lobby lobby) {
        TaskProducer producer = producers.get(lobby.getGameConfiguration().getTaskTypes().get(lobby.getRound()));
        TaskDTO task = producer.produce();
        while (producer.count() > lobby.getGameConfiguration().getTaskTypes().size() && lobby.getPreviousTasks().contains(task)) {
            task = producer.produce();
        }
        return task;
    }
}
