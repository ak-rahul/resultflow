package com.rahulak.srms.exception;

import com.rahulak.srms.dto.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.ui.Model;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.stream.Collectors;

/**
 * Global exception handler using {@code @ControllerAdvice}.
 *
 * <p>Handles both REST (JSON response) and MVC (Thymeleaf redirect/model) cases.
 * REST endpoints are identified by the request path starting with {@code /api/}.
 * MVC endpoints redirect to the error view.
 *
 * <h3>Handled exceptions:</h3>
 * <ul>
 *   <li>{@link ResourceNotFoundException} → HTTP 404</li>
 *   <li>{@link DuplicateEnrollmentException} → HTTP 409</li>
 *   <li>{@link InvalidMarksException} → HTTP 400</li>
 *   <li>{@link MethodArgumentNotValidException} → HTTP 400 with field errors</li>
 *   <li>{@link Exception} (catch-all) → HTTP 500</li>
 * </ul>
 */
@ControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    // ── ResourceNotFoundException → 404 ──────────────────────────────────────

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Object handleNotFound(ResourceNotFoundException ex,
                                 HttpServletRequest request,
                                 Model model) {
        log.warn("Resource not found at {}: {}", request.getRequestURI(), ex.getMessage());
        if (isRestRequest(request)) {
            return toErrorResponse(ex, 404, request);
        }
        model.addAttribute("errorTitle", "Not Found");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // ── DuplicateEnrollmentException → 409 ───────────────────────────────────

    @ExceptionHandler(DuplicateEnrollmentException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public Object handleDuplicate(DuplicateEnrollmentException ex,
                                  HttpServletRequest request,
                                  Model model) {
        log.warn("Duplicate enrollment attempt at {}: {}", request.getRequestURI(), ex.getMessage());
        if (isRestRequest(request)) {
            return toErrorResponse(ex, 409, request);
        }
        model.addAttribute("errorTitle", "Duplicate Enrollment");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // ── InvalidMarksException → 400 ──────────────────────────────────────────

    @ExceptionHandler(InvalidMarksException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Object handleInvalidMarks(InvalidMarksException ex,
                                     HttpServletRequest request,
                                     Model model) {
        log.warn("Invalid marks at {}: {}", request.getRequestURI(), ex.getMessage());
        if (isRestRequest(request)) {
            return toErrorResponse(ex, 400, request);
        }
        model.addAttribute("errorTitle", "Invalid Marks");
        model.addAttribute("errorMessage", ex.getMessage());
        return "error";
    }

    // ── Bean Validation → 400 with field errors ───────────────────────────────

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ResponseBody
    public ErrorResponse handleValidation(MethodArgumentNotValidException ex,
                                          HttpServletRequest request) {
        String fieldErrors = ex.getBindingResult().getFieldErrors().stream()
            .map(FieldError::getDefaultMessage)
            .collect(Collectors.joining("; "));
        log.warn("Validation error at {}: {}", request.getRequestURI(), fieldErrors);
        return new ErrorResponse(400, fieldErrors, request.getRequestURI());
    }

    // ── Catch-all → 500 ──────────────────────────────────────────────────────

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Object handleGeneral(Exception ex,
                                HttpServletRequest request,
                                Model model) {
        log.error("Unhandled exception at {}: ", request.getRequestURI(), ex);
        if (isRestRequest(request)) {
            return toErrorResponse(ex, 500, request);
        }
        model.addAttribute("errorTitle", "Internal Server Error");
        model.addAttribute("errorMessage", "Something went wrong. Please try again later.");
        return "error";
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    /** Returns true if the request originated from a REST endpoint (path starts with /api/). */
    private boolean isRestRequest(HttpServletRequest request) {
        return request.getRequestURI().startsWith("/api/");
    }

    @ResponseBody
    private ErrorResponse toErrorResponse(Exception ex, int status, HttpServletRequest request) {
        return new ErrorResponse(status, ex.getMessage(), request.getRequestURI());
    }
}
