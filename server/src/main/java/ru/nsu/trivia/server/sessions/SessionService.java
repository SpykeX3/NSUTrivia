package ru.nsu.trivia.server.sessions;

import java.nio.charset.Charset;
import java.util.Random;

import com.google.common.hash.Hashing;
import org.springframework.transaction.annotation.Transactional;

public class SessionService {

    private final SessionRepository sessionRepository;
    private final Random rng = new Random();


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

    @Transactional
    public String createSession() {
        SessionData session = new SessionData();
        session.setToken(generateToken());
        sessionRepository.saveAndFlush(session);
        return session.getToken();
    }


    private String generateToken() {
        return Hashing.sha512()
                .hashString("" + rng.nextLong() + ":" + System.currentTimeMillis(), Charset.defaultCharset())
                .toString();
    }
}
