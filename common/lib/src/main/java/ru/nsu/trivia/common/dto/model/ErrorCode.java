package ru.nsu.trivia.common.dto.model;

public enum ErrorCode {
    OK,
    UNKNOWN_ERROR,
    UNKNOWN_TOKEN,
    WRONG_ROOM_CODE,
    PLAYER_ALREADY_IN_LOBBY,
    WRONG_ANSWER_ROUND,
    GAME_ALREADY_STARTED,
    PLAYER_NOT_IN_LOBBY,
    ACTION_DENIED
}
