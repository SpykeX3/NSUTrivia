package ru.nsu.trivia.server.sessions;

import org.springframework.transaction.annotation.Transactional;

public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    @Transactional
    public void setNickname(String token, String username) {
        SessionData session = sessionRepository.findById(token)
                .orElseThrow(() -> new RuntimeException("No session found for token " + token));
        session.setNickname(username);
        sessionRepository.saveAndFlush(session);
    }

    public String getNickname(String token) {
        return sessionRepository.findById(token)
                .orElseThrow(() -> new RuntimeException("No session found for token " + token))
                .getNickname();
    }
}
