package ru.practicum.ewm.exception;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
    private String timestamp;
    private int status;
    private String reason;
    private String message;
    private String errors;
}