package com.keerthimac.bill_tracker_system.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ErrorResponseDTO {

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd hh:mm:ss a")
    private LocalDateTime timestamp;
    private int status;
    private String error; // e.g., "Not Found", "Bad Request"
    private String message;
    private String path;
    private List<String> validationErrors; // For validation error details

    public ErrorResponseDTO(HttpStatus httpStatus, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = httpStatus.value();
        this.error = httpStatus.getReasonPhrase();
        this.message = message;
        this.path = path;
    }

    public ErrorResponseDTO(HttpStatus httpStatus, String message, String path, List<String> validationErrors) {
        this(httpStatus, message, path);
        this.validationErrors = validationErrors;
    }
}
