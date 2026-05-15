package com.rahulak.srms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a grade for a specific enrollment.
 *
 * <p>Maps to the {@code grades} table.
 * Each enrollment can have at most one grade ({@code @OneToOne} from the enrollment side).
 *
 * <p>The {@code grade}, {@code gradePoint}, and {@code isPass} fields are computed
 * by the application via {@link com.rahulak.srms.util.GradeCalculator} — never entered
 * directly by the user.
 */
@Entity
@Table(name = "grades")
public class Grade {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "enrollment_id", nullable = false, unique = true)
    private Enrollment enrollment;

    @Column(name = "marks_obtained", nullable = false, precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Marks must be between 0 and 100")
    @DecimalMax(value = "100.0", message = "Marks must be between 0 and 100")
    private Double marksObtained;

    /** Computed by {@link com.rahulak.srms.util.GradeCalculator#calculateGrade(double)}. */
    @Column(name = "grade", length = 5)
    private String grade;

    /** Computed by {@link com.rahulak.srms.util.GradeCalculator#calculateGradePoint(double)}. */
    @Column(name = "grade_point", precision = 4, scale = 2)
    private Double gradePoint;

    /** {@code true} if marks ≥ 40. */
    @Column(name = "is_pass")
    private Boolean isPass;

    @Column(name = "entered_at", updatable = false)
    private LocalDateTime enteredAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        enteredAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public Grade() {}

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Enrollment getEnrollment() { return enrollment; }
    public void setEnrollment(Enrollment enrollment) { this.enrollment = enrollment; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getGradePoint() { return gradePoint; }
    public void setGradePoint(Double gradePoint) { this.gradePoint = gradePoint; }

    public Boolean getIsPass() { return isPass; }
    public void setIsPass(Boolean isPass) { this.isPass = isPass; }

    public LocalDateTime getEnteredAt() { return enteredAt; }
    public void setEnteredAt(LocalDateTime enteredAt) { this.enteredAt = enteredAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    @Override
    public String toString() {
        return "Grade{id=" + id + ", marks=" + marksObtained + ", grade='" + grade + "', isPass=" + isPass + "}";
    }
}
