package ru.practicum.shareit.util;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.EmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotValidException;

@Slf4j
public class Log {

    public static void andThrowNotValid(String message){
        log.warn(message);
        throw new NotValidException(message);
    }

    public static void andThrowNotFound(String message){
        log.warn(message);
        throw new NotFoundException(message);
    }

    public static void andThrowEmailConflict(String message){
        log.warn(message);
        throw new EmailException(message);
    }
}
