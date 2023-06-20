package ru.practicum.gateway.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {

    @ExceptionHandler(UnsupportedStateException.class)
    public ResponseEntity<Response> handleUnsupportedStateException() {
        Response response = new Response("Unknown state: UNSUPPORTED_STATUS");
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleNotValidException(NotValidException e) {
        log.error("Ошибка: {}", e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Ошибка: {}", e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<HttpStatus, String> handleConstraintViolationException(final ConstraintViolationException e) {
        log.error("Ошибка: {}", e.getMessage());
        return Map.of(HttpStatus.BAD_REQUEST, e.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<HttpStatus, String> handleThrowable(final Throwable e) {
        log.error("Ошибка: {}", e.getMessage());
        return Map.of(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
    }
}
