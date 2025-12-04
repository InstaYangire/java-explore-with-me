package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

public class StatsErrorHandler {

    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private String now() {
        return LocalDateTime.now().format(FORMATTER);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException e) {
        return new ResponseEntity<>(
                Map.of(
                        "timestamp", now(),
                        "status", 400,
                        "error", "Bad Request",
                        "message", e.getMessage(),
                        "path", "/stats"
                ),
                HttpStatus.BAD_REQUEST
        );
    }
}
