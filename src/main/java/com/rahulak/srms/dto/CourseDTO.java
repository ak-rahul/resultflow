package com.rahulak.srms.dto;

import com.rahulak.srms.model.CourseType;
import jakarta.validation.constraints.*;

/**
 * Data Transfer Object for course create/update operations and API responses.
 */
public class CourseDTO {

    private Long id;

    @NotBlank(message = "Course code is required")
    @Size(max = 20, message = "Course code must not exceed 20 characters")
    private String courseCode;

    @NotBlank(message = "Course name is required")
    @Size(max = 150, message = "Course name must not exceed 150 characters")
    private String courseName;

    @NotNull(message = "Credits are required")
    @Min(value = 1, message = "Credits must be at least 1")
    @Max(value = 6, message = "Credits must not exceed 6")
    private Integer credits;

    @NotBlank(message = "Department is required")
    private String department;

    private CourseType courseType = CourseType.THEORY;

    private Boolean isActive = Boolean.TRUE;

    // ── Constructors ─────────────────────────────────────────────────────────

    public CourseDTO() {}

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
}
