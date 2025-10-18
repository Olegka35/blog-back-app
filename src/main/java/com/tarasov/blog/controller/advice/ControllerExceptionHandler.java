package com.tarasov.blog.controller.advice;


import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class ControllerExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<String> handleResponseStatus(ResponseStatusException ex) {
        log.error(ex.getMessage(), ex);
        return ResponseEntity.status(ex.getStatusCode()).body(ex.getReason());
    }

    @ExceptionHandler({ MissingServletRequestParameterException.class, IllegalArgumentException.class })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, Object> handleMissedParameterException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return Map.of("message", exception.getMessage() != null ? exception.getMessage() : "Unexpected error",
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, Object> handleException(Exception exception) {
        log.error(exception.getMessage(), exception);
        return Map.of("error", exception.getClass().getName(),
                "message", exception.getMessage() != null ? exception.getMessage() : "Unexpected error",
                "timestamp", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
    }
}
