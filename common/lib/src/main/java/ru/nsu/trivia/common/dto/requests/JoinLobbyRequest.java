package ru.nsu.trivia.common.dto.requests;

public class JoinLobbyRequest extends UsingTokenRequest {
    String roomID;

    public String getRoomID() {
        return roomID;
    }

    public void setRoomID(String roomID) {
        this.roomID = roomID;
    }
}
