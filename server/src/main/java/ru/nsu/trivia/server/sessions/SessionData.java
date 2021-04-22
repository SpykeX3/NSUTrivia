package ru.nsu.trivia.server.sessions;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class SessionData {
    @Id
    private String token;
    private String nickname;
    private Long lobbyId;

    public SessionData() {
    }

    public SessionData(String token, String nickname, Long lobbyId) {
        this.token = token;
        this.nickname = nickname;
        this.lobbyId = lobbyId;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public Long getLobbyId() {
        return lobbyId;
    }

    public void setLobbyId(Long lobbyId) {
        this.lobbyId = lobbyId;
    }
}
