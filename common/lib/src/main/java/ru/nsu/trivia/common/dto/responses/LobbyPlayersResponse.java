package ru.nsu.trivia.common.dto.responses;

import java.util.List;

import ru.nsu.trivia.common.dto.model.PlayerInLobby;

public class LobbyPlayersResponse {
    List<PlayerInLobby> playerList;

    public List<PlayerInLobby> getPlayerList() {
        return playerList;
    }

    public void setPlayerList(List<PlayerInLobby> playerList) {
        this.playerList = playerList;
    }
}
