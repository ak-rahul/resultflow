package com.rahulak.srms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDateTime;

/**
 * JPA entity representing a student's enrollment in a course for a specific semester.
 *
 * <p>Maps to the {@code enrollments} table.
 * A unique constraint {@code uq_enrollment} prevents a student from being enrolled
 * in the same course more than once per semester and academic year.
 */
@Entity
@Table(
    name = "enrollments",
    uniqueConstraints = @UniqueConstraint(
        name = "uq_enrollment",
        columnNames = {"student_id", "course_id", "semester", "academic_year"}
    )
)
public class Enrollment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @Column(name = "semester", nullable = false)
    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer semester;

    @Column(name = "academic_year", nullable = false, length = 10)
    @NotBlank(message = "Academic year is required (e.g. 2024-25)")
    private String academicYear;

    @Column(name = "enrolled_at", updatable = false)
    private LocalDateTime enrolledAt;

    @OneToOne(mappedBy = "enrollment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Grade grade;

    // ── Lifecycle ────────────────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        enrolledAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public Enrollment() {}

    public Enrollment(Student student, Course course, Integer semester, String academicYear) {
        this.student = student;
        this.course = course;
        this.semester = semester;
        this.academicYear = academicYear;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }

    public Course getCourse() { return course; }
    public void setCourse(Course course) { this.course = course; }

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public LocalDateTime getEnrolledAt() { return enrolledAt; }
    public void setEnrolledAt(LocalDateTime enrolledAt) { this.enrolledAt = enrolledAt; }

    public Grade getGrade() { return grade; }
    public void setGrade(Grade grade) { this.grade = grade; }

    @Override
    public String toString() {
        return "Enrollment{id=" + id + ", semester=" + semester + ", academicYear='" + academicYear + "'}";
    }
}
