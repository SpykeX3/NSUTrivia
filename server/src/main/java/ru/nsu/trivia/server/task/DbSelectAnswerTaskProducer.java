package ru.nsu.trivia.server.task;

import java.util.Random;

import org.springframework.data.domain.PageRequest;
import ru.nsu.trivia.common.dto.model.task.TaskDTO;
import ru.nsu.trivia.server.model.SelectAnswerTask;
import ru.nsu.trivia.server.model.converters.SelectAnswerTaskConverter;

public class DbSelectAnswerTaskProducer implements TaskProducer {

    private final SelectAnswerTaskRepository repository;
    private final Random rng = new Random();

    public DbSelectAnswerTaskProducer(SelectAnswerTaskRepository selectAnswerTaskRepository) {
        this.repository = selectAnswerTaskRepository;
    }

    @Override
    public TaskDTO produce() {
        SelectAnswerTask task = null;
        while (task == null) {
            var count = (int) repository.count();
            if (count == 0) {
                throw new RuntimeException("No tasks in the database for type: " + TaskDTO.Type.Select_answer.name());
            }
            int questionId = rng.nextInt(count);
            var page = repository.findAll(PageRequest.of(questionId, 1));
            if (page.hasContent()) {
                task = page.getContent().get(0);
            }
        }
        return SelectAnswerTaskConverter.convert(task);
    }
}
