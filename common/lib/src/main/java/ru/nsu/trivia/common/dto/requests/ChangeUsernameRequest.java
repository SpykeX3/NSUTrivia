package ru.nsu.trivia.common.dto.requests;

public class ChangeUsernameRequest extends UsingTokenRequest{
    private String username;

    public ChangeUsernameRequest() {
    }

    public ChangeUsernameRequest(String token, String username) {
        super(token);
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
