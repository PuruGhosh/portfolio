package com.portfolio.gateway_security.exception;

import com.portfolio.gateway_security.representation.dto.ResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reactor.core.publisher.Mono;

import java.util.List;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public Mono<ResponseEntity<ResponseDto<Void>>> handleUserNotFound(UserNotFoundException ex) {
        log.error(ex.getMessage());
        var isOptionalMessage = !CollectionUtils.isEmpty(ex.getMessages());
        return buildErrorResponse(isOptionalMessage? ex.getMessages(): List.of(ex.getMessage()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(UnauthorizedAccessException.class)
    public Mono<ResponseEntity<ResponseDto<Void>>> handleUnauthorized(UnauthorizedAccessException ex) {
        log.error(ex.getMessage());
        var isOptionalMessage = !CollectionUtils.isEmpty(ex.getMessages());
        return buildErrorResponse(isOptionalMessage? ex.getMessages(): List.of(ex.getMessage()), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(RuntimeException.class)
    public Mono<ResponseEntity<ResponseDto<Void>>> handleGenericRuntime(Exception ex) {

        return buildErrorResponse(
                List.of("Something went wrong: " + ex.getMessage()), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ResponseDto<Void>>> handleGeneric(Exception ex) {
        log.error("exception",ex);
        return buildErrorResponse(
                List.of("Something went wrong: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    private Mono<ResponseEntity<ResponseDto<Void>>> buildErrorResponse(List<String> messages, HttpStatus status) {
        return Mono.just(ResponseEntity.status(status).body(ResponseDto.failure(messages)));
    }
}
