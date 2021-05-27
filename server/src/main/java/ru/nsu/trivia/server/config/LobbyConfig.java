package ru.nsu.trivia.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.nsu.trivia.server.task.DbSelectAnswerTaskProducer;
import ru.nsu.trivia.server.task.DbSetNearestAnswerTaskProducer;
import ru.nsu.trivia.server.task.DbTaskService;
import ru.nsu.trivia.server.task.HardcodedTaskService;
import ru.nsu.trivia.server.lobby.LobbyService;
import ru.nsu.trivia.server.task.SelectAnswerTaskRepository;
import ru.nsu.trivia.server.task.SetNearestAnswerTaskRepository;
import ru.nsu.trivia.server.task.TaskService;
import ru.nsu.trivia.server.sessions.SessionService;

@Configuration
@EnableScheduling
@PropertySource("classpath:application.properties")
public class LobbyConfig {
    @Value("${lobby.closed.lifetime}")
    private long CLOSED_LOBBY_LIFETIME;
    @Value("${lobby.alive.lifetime}")
    private long ACTIVE_LOBBY_LIFETIME;

    @Bean
    LobbyService lobbyService(SessionService sessionService, TaskService taskService) {
        return new LobbyService(sessionService, taskService, CLOSED_LOBBY_LIFETIME, ACTIVE_LOBBY_LIFETIME);
    }

    @Bean
    DbSelectAnswerTaskProducer dbSelectAnswerTaskProducer(SelectAnswerTaskRepository selectAnswerTaskRepository) {
        return new DbSelectAnswerTaskProducer(selectAnswerTaskRepository);
    }

    @Bean
    DbSetNearestAnswerTaskProducer dbSetNearestAnswerTaskProducer(
            SetNearestAnswerTaskRepository setNearestAnswerTaskRepository) {
        return new DbSetNearestAnswerTaskProducer(setNearestAnswerTaskRepository);
    }

    @Bean
    TaskService taskService(DbSelectAnswerTaskProducer dbSelectAnswerTaskProducer,
                            DbSetNearestAnswerTaskProducer dbSetNearestAnswerTaskProducer) {
        return new DbTaskService(dbSelectAnswerTaskProducer, dbSetNearestAnswerTaskProducer);
    }


}
