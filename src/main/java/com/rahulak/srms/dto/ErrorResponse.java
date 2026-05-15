package com.rahulak.srms.dto;

import java.time.LocalDateTime;

/**
 * Standard error response body for REST API error responses.
 *
 * <p>Returned by {@link com.rahulak.srms.exception.GlobalExceptionHandler}
 * for all exception types.
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "timestamp": "2025-05-15T10:30:00",
 *   "status": 404,
 *   "message": "Student with roll number 23CS999 not found",
 *   "path": "/api/students/23CS999"
 * }
 * </pre>
 */
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;

    // ── Constructors ─────────────────────────────────────────────────────────

    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public ErrorResponse(int status, String message, String path) {
        this.timestamp = LocalDateTime.now();
        this.status = status;
        this.message = message;
        this.path = path;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public int getStatus() { return status; }
    public void setStatus(int status) { this.status = status; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }
}
