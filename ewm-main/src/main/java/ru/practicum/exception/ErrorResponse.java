package ru.practicum.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ErrorResponse {
    private final String status;
    private final String reason;
    private final String message;
    private final String timestamp;
    private final String stackTrace;
}
