package ru.nsu.trivia.server.controllers;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.nsu.trivia.common.dto.StatusResponse;

@RestController(value = "/user/")
public class UserController {
    @PostMapping(value = "nickname", consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    public StatusResponse setUsername(String username) {
        return new StatusResponse(0);
    }
}
