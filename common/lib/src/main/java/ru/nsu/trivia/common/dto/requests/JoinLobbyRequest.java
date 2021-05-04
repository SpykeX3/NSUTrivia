package ru.nsu.trivia.common.dto.requests;

public class JoinLobbyRequest extends UsingTokenRequest {
    String roomID;

    public JoinLobbyRequest() {
        super();
    }

    public JoinLobbyRequest(String token, String roomID) {
        super(token);
        this.roomID = roomID;
    }

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
