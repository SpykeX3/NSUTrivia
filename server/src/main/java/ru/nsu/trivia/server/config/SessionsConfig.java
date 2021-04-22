package ru.nsu.trivia.server.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.nsu.trivia.server.sessions.SessionRepository;
import ru.nsu.trivia.server.sessions.SessionService;

@Configuration
public class SessionsConfig {
    @Bean
    SessionService sessionService(SessionRepository sessionRepository) {
        return new SessionService(sessionRepository);
    }
}
