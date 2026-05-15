package com.rahulak.srms.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Thrown when a student is already enrolled in the same course
 * for the same semester and academic year.
 *
 * <p>Caught by {@link GlobalExceptionHandler} which returns HTTP 409 for REST requests.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateEnrollmentException extends RuntimeException {

    public DuplicateEnrollmentException(String message) {
        super(message);
    }

    public DuplicateEnrollmentException(String rollNumber, String courseCode, int semester, String year) {
        super(String.format(
            "Student '%s' is already enrolled in course '%s' for semester %d (%s)",
            rollNumber, courseCode, semester, year));
    }
}
