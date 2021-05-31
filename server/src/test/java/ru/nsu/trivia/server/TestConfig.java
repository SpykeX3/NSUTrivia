package ru.nsu.trivia.server;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.nsu.trivia.server.task.HardcodedTaskService;
import ru.nsu.trivia.server.task.TaskService;

@Configuration
public class TestConfig {
    @Bean
    public TaskService taskService() {
        return new HardcodedTaskService();
    }
}
