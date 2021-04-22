package ru.nsu.trivia.common.dto.requests;

public class GetByTokenRequest {
    String token;

    public GetByTokenRequest(String token) {
        this.token = token;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }
}
