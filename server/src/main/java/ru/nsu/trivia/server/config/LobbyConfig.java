package ru.nsu.trivia.server.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.scheduling.annotation.EnableScheduling;
import ru.nsu.trivia.server.lobby.HardcodedTaskService;
import ru.nsu.trivia.server.lobby.LobbyService;
import ru.nsu.trivia.server.lobby.TaskService;
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
    TaskService taskService() {
        return new HardcodedTaskService();
    }
}
