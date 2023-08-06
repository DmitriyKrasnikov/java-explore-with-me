package ru.practicum.ewm.exception;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
@Slf4j
public class ErrorHandler {
    public static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @ExceptionHandler
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse badRequest(NotFoundException e) {
        log.warn("Получен статус 404 NotFound. Ошибка : {}", e.getMessage());
        return new ErrorResponse("NOT_FOUND", "The required object was not found.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse forbidden(ValidateException e) {
        log.warn("Получен статус 403 Forbidden. Ошибка : {}", e.getMessage());
        return new ErrorResponse("FORBIDDEN", "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler({ConflictException.class, DataIntegrityViolationException.class})
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse conflict(Exception e) {
        log.warn("Получен статус 409 Conflict. Ошибка : {}", e.getMessage());
        return new ErrorResponse("CONFLICT", "For the requested operation the conditions are not met.",
                e.getMessage(), LocalDateTime.now());
    }

    @Data
    class ErrorResponse {
        String status;
        String reason;
        String message;
        String timestamp;

        public ErrorResponse(String status, String reason, String message, LocalDateTime timestamp) {
            this.status = status;
            this.reason = reason;
            this.message = message;
            this.timestamp = timestamp.format(DATE_TIME_FORMAT);
        }
    }
}
