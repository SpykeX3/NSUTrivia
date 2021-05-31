package ru.nsu.trivia.common.dto.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
    public int code;
    public String error;

    public StatusResponse() {
    }

    public StatusResponse(int code) {
        this.code = code;
    }

    public StatusResponse(int code, String errors) {
        this.code = code;
        this.error = errors;
    }

    public void setErrors(String errors) {
        this.error = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrors() {
        return error;
    }
}
