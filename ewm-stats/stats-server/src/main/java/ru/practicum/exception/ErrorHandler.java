package ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.validation.ConstraintViolationException;

@RestControllerAdvice("ru.practicum")
@Slf4j
public class ErrorHandler {

    @ExceptionHandler({
            ConstraintViolationException.class,
            ValidationException.class,
            MethodArgumentNotValidException.class,
            IllegalArgumentException.class,
            HttpMessageNotReadableException.class,
            IllegalStateException.class})
    @ResponseStatus(code = HttpStatus.BAD_REQUEST)
    public ErrorResponse handleConstraintViolationException(final RuntimeException e) {
        log.error("Error (Bad Request): " + e.getClass());
        return new ErrorResponse(e.getClass().toString(), e.getMessage());
    }

    @ExceptionHandler
    @ResponseStatus(code = HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse generalError(final Throwable e) {
        log.error("Error (Internal Server Error): " + e.getClass() + "\n" + e.getMessage());
        return new ErrorResponse(e.getClass().toString(), e.getMessage());
    }

}
