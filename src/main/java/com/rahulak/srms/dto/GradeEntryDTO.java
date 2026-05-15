package com.rahulak.srms.dto;

import jakarta.validation.constraints.*;

/**
 * DTO for entering or updating a grade for a specific enrollment.
 *
 * <p>Only {@code enrollmentId} and {@code marksObtained} are provided by the user.
 * Grade, grade point, and pass/fail are computed by
 * {@link com.rahulak.srms.util.GradeCalculator}.
 */
public class GradeEntryDTO {

    @NotNull(message = "Enrollment ID is required")
    private Long enrollmentId;

    @NotNull(message = "Marks are required")
    @DecimalMin(value = "0.0", message = "Marks must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Marks must be between 0 and 100")
    private Double marksObtained;

    // ── Constructors ─────────────────────────────────────────────────────────

    public GradeEntryDTO() {}

    public GradeEntryDTO(Long enrollmentId, Double marksObtained) {
        this.enrollmentId = enrollmentId;
        this.marksObtained = marksObtained;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(Long enrollmentId) { this.enrollmentId = enrollmentId; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }
}
