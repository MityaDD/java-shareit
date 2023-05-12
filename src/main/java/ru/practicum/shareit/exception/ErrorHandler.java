package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class ErrorHandler {

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> handleNotValidExceptionHandler(NotValidException e) {
        log.error("Неверный параметр: " + e.getMessage());
        return Map.of("error", String.format("Неверный параметр %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String, String> handleNotFoundExceptionHandler(NotFoundException e) {
        log.error("Не найдено: " + e.getMessage());
        return Map.of("error", String.format("Не найден %s", e.getMessage()));
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public Map<String, String> handleEmailExceptionHandler(EmailException e) {
        log.error("Ошибка: " + e.getMessage());
        return Map.of("error", e.getMessage());
    }

}
