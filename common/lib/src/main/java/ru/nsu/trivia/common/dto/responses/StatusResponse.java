package ru.nsu.trivia.common.dto.responses;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class StatusResponse {
    public int code;
    public List<String> errors;

    public StatusResponse() {
    }

    public StatusResponse(int code) {
        this.code = code;
    }

    public StatusResponse(int code, List<String> errors) {
        this.code = code;
        this.errors = errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public List<String> getErrors() {
        return errors;
    }
}
