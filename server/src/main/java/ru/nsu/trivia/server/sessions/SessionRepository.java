package ru.nsu.trivia.server.sessions;

import org.springframework.data.jpa.repository.JpaRepository;

public interface SessionRepository extends JpaRepository<SessionData, String> {
}
