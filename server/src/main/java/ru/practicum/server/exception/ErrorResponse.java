package ru.practicum.server.exception;

import lombok.Data;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Data
public class ErrorResponse {
    private final String error;
    private String description;
}