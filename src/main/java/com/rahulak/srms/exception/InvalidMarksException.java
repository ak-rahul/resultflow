package com.rahulak.srms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when marks provided for a grade entry are outside the valid range [0, 100].
 *
 * <p>Caught by {@link GlobalExceptionHandler} which returns HTTP 400 for REST requests.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidMarksException extends RuntimeException {

    public InvalidMarksException(String message) {
        super(message);
    }

    public InvalidMarksException(double marks) {
        super(String.format("Invalid marks: %.2f. Marks must be between 0 and 100.", marks));
    }
}
