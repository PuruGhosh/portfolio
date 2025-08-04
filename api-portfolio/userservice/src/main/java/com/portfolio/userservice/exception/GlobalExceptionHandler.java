package com.portfolio.userservice.exception;

import com.portfolio.userservice.entity.dto.ResponseDto;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

  @ExceptionHandler(UserNotFoundException.class)
  public ResponseEntity<ResponseDto<Void>> handleUserNotFound(UserNotFoundException ex) {
    log.error(ex.getMessage());
    var isOptionalMessage = !CollectionUtils.isEmpty(ex.getMessages());
    return buildErrorResponse(isOptionalMessage? ex.getMessages(): List.of(ex.getMessage()), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(UnauthorizedAccessException.class)
  public ResponseEntity<ResponseDto<Void>> handleUnauthorized(UnauthorizedAccessException ex) {
    log.error(ex.getMessage());
    var isOptionalMessage = !CollectionUtils.isEmpty(ex.getMessages());
    return buildErrorResponse(isOptionalMessage? ex.getMessages(): List.of(ex.getMessage()), HttpStatus.UNAUTHORIZED);
  }

  @ExceptionHandler(RuntimeException.class)
  public ResponseEntity<ResponseDto<Void>> handleGenericRuntime(Exception ex) {

    return buildErrorResponse(
            List.of("Something went wrong: " + ex.getMessage()), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ResponseDto<Void>> handleGeneric(Exception ex) {
    log.error("exception",ex);
    return buildErrorResponse(
        List.of("Something went wrong: " + ex.getMessage()), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  private ResponseEntity<ResponseDto<Void>> buildErrorResponse(List<String> messages, HttpStatus status) {
    return new ResponseEntity<>(ResponseDto.failure(messages), status);
  }
}
