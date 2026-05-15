package com.rahulak.srms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing an academic course.
 *
 * <p>Maps to the {@code courses} table.
 * One course can have many enrollments across different semesters and students.
 */
@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "course_code", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String courseCode;

    @Column(name = "course_name", nullable = false, length = 150)
    @NotBlank(message = "Course name is required")
    @Size(max = 150, message = "Course name must not exceed 150 characters")
    private String courseName;

    @Column(name = "credits", nullable = false)
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits must not exceed 6")
    private Integer credits;

    @Column(name = "department", nullable = false, length = 100)
    @NotBlank(message = "Department is required")
    private String department;

    @Enumerated(EnumType.STRING)
    @Column(name = "course_type", length = 10)
    private CourseType courseType = CourseType.THEORY;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    // ── Constructors ─────────────────────────────────────────────────────────

    public Course() {}

    public Course(String courseCode, String courseName, Integer credits, String department, CourseType courseType) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.department = department;
        this.courseType = courseType;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public CourseType getCourseType() { return courseType; }
    public void setCourseType(CourseType courseType) { this.courseType = courseType; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    @Override
    public String toString() {
        return "Course{id=" + id + ", courseCode='" + courseCode + "', courseName='" + courseName + "'}";
    }
}
