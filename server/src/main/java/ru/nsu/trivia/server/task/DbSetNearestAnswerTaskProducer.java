package ru.nsu.trivia.server.task;

import java.util.Random;

import org.springframework.data.domain.PageRequest;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.SetNearestAnswerTask;
import ru.nsu.trivia.server.model.converters.SetNearestAnswerTaskConverter;

public class DbSetNearestAnswerTaskProducer implements TaskProducer {

    private final SetNearestAnswerTaskRepository repository;
    private final Random rng = new Random();

    public DbSetNearestAnswerTaskProducer(SetNearestAnswerTaskRepository repository) {
        this.repository = repository;
    }

    @Override
    public TaskDTO produce() {
        SetNearestAnswerTask task = null;
        while (task == null) {
            var count = (int) repository.count();
            if (count == 0) {
                throw new RuntimeException("No tasks in the database for type: " + TaskDTO.Type.Type_answer.name());
            }
            int questionId = rng.nextInt(count);
            var page = repository.findAll(PageRequest.of(questionId, 1));
            if (page.hasContent()) {
                task = page.getContent().get(0);
            }
        }
        return SetNearestAnswerTaskConverter.convert(task);
    }

    @Override
    public long count() {
        return repository.count();
    }
}
