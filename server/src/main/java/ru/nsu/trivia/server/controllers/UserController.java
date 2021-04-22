package ru.nsu.trivia.server.controllers;

import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.trivia.common.dto.requests.ChangeUsernameRequest;
import ru.nsu.trivia.common.dto.requests.GetByTokenRequest;
import ru.nsu.trivia.common.dto.responses.StatusResponse;
import ru.nsu.trivia.server.sessions.SessionService;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    SessionService sessionService;

    @PostMapping(value = "/nickname", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse setUsername(@RequestBody ChangeUsernameRequest changeUsernameRequest) {
        sessionService.setNickname(changeUsernameRequest.getToken(), changeUsernameRequest.getUsername());
        return new StatusResponse(0);
    }

    @GetMapping(value = "/nickname", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public String getUsername(@RequestBody GetByTokenRequest request) {
        return sessionService.getNickname(request.getToken());
    }

    @ExceptionHandler(Exception.class)
    public StatusResponse handleError(HttpServletRequest req, Exception ex) {
        return new StatusResponse(1, List.of(ex.getMessage()));
    }
}
