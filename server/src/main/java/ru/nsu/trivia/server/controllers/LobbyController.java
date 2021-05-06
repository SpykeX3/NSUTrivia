package ru.nsu.trivia.server.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.async.DeferredResult;
import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.common.dto.model.task.Answer;
import ru.nsu.trivia.common.dto.requests.JoinLobbyRequest;
import ru.nsu.trivia.common.dto.requests.UsingTokenRequest;
import ru.nsu.trivia.common.dto.responses.StatusResponse;
import ru.nsu.trivia.server.lobby.LobbyService;

@RestController
@RequestMapping("/lobby")
public class LobbyController {

    LobbyService lobbyService;

    @Autowired
    public LobbyController(LobbyService lobbyService) {
        this.lobbyService = lobbyService;
    }

    @GetMapping(value = "/get", produces =
            MediaType.APPLICATION_JSON_VALUE)
    LobbyDTO getLobbyState(@RequestParam String token) {
        return lobbyService.getLobbyByToken(token);
    }

    @GetMapping(value = "/subscribe", produces =
            MediaType.APPLICATION_JSON_VALUE)
    DeferredResult<LobbyDTO> subscribeLobbyState(@RequestParam String token,
                                                 @RequestParam(defaultValue = "0") long lastUpdate) {
        DeferredResult<LobbyDTO> result = new DeferredResult<>(360000L);
        lobbyService.addSubscription(token, lastUpdate, result);
        return result;
    }

    @PostMapping(value = "/create", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    LobbyDTO createLobby(@RequestBody UsingTokenRequest request) {
        String roomId = null;
        try {
            roomId = lobbyService.createRoom();
            lobbyService.connectToLobby(request.getToken(), roomId);
            return lobbyService.getLobbyById(roomId);
        } catch (Exception e) {
            if (roomId != null) {
                lobbyService.deleteRoom(roomId);
            }
            throw new RuntimeException(e);
        }
    }

    @PostMapping(value = "/join", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    LobbyDTO joinLobby(@RequestBody JoinLobbyRequest request) {
        lobbyService.connectToLobby(request.getToken(), request.getRoomID());
        return lobbyService.getLobbyById(request.getRoomID());
    }

    @PostMapping(value = "/leave", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    StatusResponse leaveLobby(@RequestBody UsingTokenRequest request) {
        lobbyService.leaveLobby(request.getToken());
        return new StatusResponse(0);
    }

    @PostMapping(value = "/start", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    StatusResponse startLobby(@RequestBody UsingTokenRequest request) {
        lobbyService.startLobby(request.getToken());
        return new StatusResponse(0);
    }

    @PostMapping(value = "/answer", consumes = MediaType.APPLICATION_JSON_VALUE, produces =
            MediaType.APPLICATION_JSON_VALUE)
    StatusResponse submitAnswer(@RequestBody Answer answer) {
        lobbyService.submitAnswer(answer);
        return new StatusResponse(0);
    }
}
