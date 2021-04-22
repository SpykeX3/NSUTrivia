package ru.nsu.trivia.server.controllers;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.trivia.server.sessions.SessionService;

@RestController
@RequestMapping("/token")
public class TokenController {

    @Autowired
    SessionService sessionService;

    @PostMapping(value = "/generate", produces = MediaType.APPLICATION_JSON_VALUE)
    String createSession() {
        return sessionService.createSession();
    }


}
