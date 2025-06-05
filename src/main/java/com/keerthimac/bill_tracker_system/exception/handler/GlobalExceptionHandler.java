package com.keerthimac.bill_tracker_system.exception.handler;

import com.keerthimac.bill_tracker_system.dto.ErrorResponseDTO;
import com.keerthimac.bill_tracker_system.exception.DuplicateResourceException;
import com.keerthimac.bill_tracker_system.exception.ResourceInUseException;
import com.keerthimac.bill_tracker_system.exception.ResourceNotFoundException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice // This annotation makes it a global exception handler
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    // Handler for ResourceNotFoundException
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<Object> handleResourceNotFoundException(
            ResourceNotFoundException ex, WebRequest request) {

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // Handler for DuplicateResourceException
    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<Object> handleDuplicateResourceException(
            DuplicateResourceException ex, WebRequest request) {

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT, // 409 Conflict is appropriate for duplicates
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // Handler for jakarta.validation.ConstraintViolationException (e.g., @Validated on path variables/request params)
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Object> handleConstraintViolationException(
            ConstraintViolationException ex, WebRequest request) {

        List<String> validationErrors = ex.getConstraintViolations()
                .stream()
                .map(ConstraintViolation::getMessage)
                .collect(Collectors.toList());

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST,
                "Validation failed. Check 'validationErrors' for details.",
                path,
                validationErrors
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // Override Spring's default handler for MethodArgumentNotValidException (for @Valid on @RequestBody)
    // This provides a more detailed error response for request body validation failures.
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {

        List<String> validationErrors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            validationErrors.add(error.getField() + ": " + error.getDefaultMessage());
        }
        // You could also collect global errors if any: ex.getBindingResult().getGlobalErrors()

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                (HttpStatus) status, // Use the status determined by Spring
                "Validation failed. Check 'validationErrors' for details.",
                path,
                validationErrors
        );
        return new ResponseEntity<>(errorResponse, headers, status);
    }

    // A generic handler for any other unhandled exceptions (catch-all)
    // This should be the last handler, or more specific ones might not be triggered.
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllOtherExceptions(Exception ex, WebRequest request) {
        // Log the exception here for debugging purposes
        // logger.error("An unexpected error occurred: ", ex); // Assuming you have a logger

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "An unexpected error occurred. Please try again later.", // Generic message for client
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    // In GlobalExceptionHandler.java
    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<Object> handleResourceInUseException(
            ResourceInUseException ex, WebRequest request) {

        String path = ((ServletWebRequest)request).getRequest().getRequestURI();
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.CONFLICT,
                ex.getMessage(),
                path
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }


}
