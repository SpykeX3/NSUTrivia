package ru.nsu.trivia.server.controllers;


import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.trivia.common.dto.responses.StatusResponse;
import ru.nsu.trivia.server.lobby.LobbyService;
import ru.nsu.trivia.server.sessions.SessionService;
import ru.nsu.trivia.server.util.ErrorHandler;

@RestController
@RequestMapping("/token")
public class TokenController {

    private static final Logger log = Logger.getLogger(LobbyService.class.getName());

    @Autowired
    SessionService sessionService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    String createSession() {
        return sessionService.createSession();
    }

    @ExceptionHandler
    ResponseEntity<StatusResponse> onError(Exception e) {
        log.warning(e.getMessage());
        return ErrorHandler.processError(e);
    }
}
