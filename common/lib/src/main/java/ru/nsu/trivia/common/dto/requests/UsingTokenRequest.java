package ru.nsu.trivia.common.dto.requests;

public class UsingTokenRequest {
    private String token;

    public UsingTokenRequest() {
    }

    public UsingTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
