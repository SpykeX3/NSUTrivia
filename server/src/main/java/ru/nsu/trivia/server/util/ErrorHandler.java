package ru.nsu.trivia.server.util;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import ru.nsu.trivia.common.dto.responses.StatusResponse;

public class ErrorHandler {
    public static ResponseEntity<StatusResponse> processError(Exception e) {
        e.printStackTrace();
        HttpStatus status = ErrorMapper.isUserError(e) ? HttpStatus.BAD_REQUEST : HttpStatus.INTERNAL_SERVER_ERROR;
        return ResponseEntity.status(status)
                .body(new StatusResponse(ErrorMapper.getCode(e).ordinal(), e.getMessage()));
    }
}
