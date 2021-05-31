package ru.nsu.trivia.server.util;

import java.util.Map;
import java.util.Set;

import ru.nsu.trivia.common.dto.model.ErrorCode;
import ru.nsu.trivia.server.model.error.ActionDeniedException;
import ru.nsu.trivia.server.model.error.GameAlreadyStartedException;
import ru.nsu.trivia.server.model.error.InvalidRoomCodeException;
import ru.nsu.trivia.server.model.error.PlayerAlreadyInLobbyException;
import ru.nsu.trivia.server.model.error.PlayerNotInLobbyException;
import ru.nsu.trivia.server.model.error.UnknownTokenException;
import ru.nsu.trivia.server.model.error.WrongRoundException;

public class ErrorMapper {
    private final static Map<Class<?>, ErrorCode> ERRORS_MAP = Map.of(
            UnknownTokenException.class, ErrorCode.UNKNOWN_TOKEN,
            InvalidRoomCodeException.class, ErrorCode.WRONG_ROOM_CODE,
            PlayerAlreadyInLobbyException.class, ErrorCode.PLAYER_ALREADY_IN_LOBBY,
            WrongRoundException.class, ErrorCode.WRONG_ANSWER_ROUND,
            GameAlreadyStartedException.class, ErrorCode.GAME_ALREADY_STARTED,
            PlayerNotInLobbyException.class, ErrorCode.PLAYER_NOT_IN_LOBBY,
            ActionDeniedException.class, ErrorCode.ACTION_DENIED
    );

    private final static Set<Class<?>> USER_ERRORS = Set.of(
            InvalidRoomCodeException.class,
            PlayerAlreadyInLobbyException.class,
            WrongRoundException.class,
            GameAlreadyStartedException.class,
            PlayerNotInLobbyException.class,
            ActionDeniedException.class);

    public static ErrorCode getCode(Exception e) {
        return ERRORS_MAP.getOrDefault(e.getClass(), ErrorCode.UNKNOWN_ERROR);
    }

    public static boolean isUserError(Exception e) {
        return USER_ERRORS.contains(e.getClass());
    }
}
