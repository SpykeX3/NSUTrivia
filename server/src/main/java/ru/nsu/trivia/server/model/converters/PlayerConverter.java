package ru.nsu.trivia.server.model.converters;

import ru.nsu.trivia.common.dto.model.PlayerInLobby;
import ru.nsu.trivia.server.model.Player;

public class PlayerConverter {
    public static PlayerInLobby convert(Player player) {
        PlayerInLobby dto = new PlayerInLobby();
        dto.setHost(player.isHost());
        dto.setUsername(player.getNickname());
        dto.setScore(player.getScore());
        return dto;
    }
}
