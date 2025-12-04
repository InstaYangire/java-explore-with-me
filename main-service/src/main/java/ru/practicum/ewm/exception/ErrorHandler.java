package ru.practicum.ewm.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@RestControllerAdvice
public class ErrorHandler {

    private String now() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(NotFoundException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(404)
                        .reason("The required object was not found.")
                        .message(e.getMessage())
                        .errors(null)
                        .build(),
                HttpStatus.NOT_FOUND
        );
    }

    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflict(ConflictException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(409)
                        .reason("Integrity constraint has been violated.")
                        .message(e.getMessage())
                        .errors(null)
                        .build(),
                HttpStatus.CONFLICT
        );
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(400)
                        .reason("Incorrectly made request.")
                        .message(e.getMessage())
                        .errors(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(400)
                        .reason("Incorrectly made request.")
                        .message(e.getBindingResult().getAllErrors().get(0).getDefaultMessage())
                        .errors(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ErrorResponse> handleOther(Throwable e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(500)
                        .reason("Unexpected error.")
                        .message(e.getMessage())
                        .errors(null)
                        .build(),
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }

    @ExceptionHandler(org.springframework.web.bind.MissingServletRequestParameterException.class)
    public ResponseEntity<ErrorResponse> handleMissingParam(MissingServletRequestParameterException e) {
        return new ResponseEntity<>(
                ErrorResponse.builder()
                        .timestamp(now())
                        .status(400)
                        .reason("Incorrectly made request.")
                        .message(e.getMessage())
                        .errors(null)
                        .build(),
                HttpStatus.BAD_REQUEST
        );
    }
}
