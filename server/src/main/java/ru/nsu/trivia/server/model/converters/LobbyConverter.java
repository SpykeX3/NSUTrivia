package ru.nsu.trivia.server.model.converters;

import java.util.stream.Collectors;

import ru.nsu.trivia.common.dto.model.LobbyDTO;
import ru.nsu.trivia.server.model.Lobby;

public class LobbyConverter {
    public static LobbyDTO convert(Lobby lobby) {
        LobbyDTO dto = new LobbyDTO();
        dto.setPlayers(lobby.getPlayers().stream().map(PlayerConverter::convert).collect(Collectors.toList()));
        dto.setRound(lobby.getRound());
        dto.setState(lobby.getState());
        dto.setId(lobby.getId());
        dto.setCurrentTask(lobby.getCurrentTask());
        dto.setLastUpdated(lobby.getLastUpdate());
        return dto;
    }
}
