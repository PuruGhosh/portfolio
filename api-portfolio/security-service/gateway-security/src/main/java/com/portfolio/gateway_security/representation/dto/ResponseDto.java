package com.portfolio.gateway_security.representation.dto;

import java.time.LocalDateTime;
import java.util.List;

public class ResponseDto<T> {

    private T data;
    private boolean error;

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<String> getErrors() {
        return errors;
    }

    public void setErrors(List<String> errors) {
        this.errors = errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    private List<String> errors;

    private LocalDateTime timestamp = LocalDateTime.now();

    public ResponseDto() {
    }

    public ResponseDto(T data) {
        this.data = data;
        this.error = false;
    }

    public ResponseDto(List<String> errors) {
        this.error = true;
        this.errors = errors;
    }

    public static <T> ResponseDto<T> success(T data) {
        return new ResponseDto<>(data);
    }

    public static <T> ResponseDto<T> failure(List<String> errors) {
        return new ResponseDto<>(errors);
    }

}
