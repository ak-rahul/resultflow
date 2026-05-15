package com.rahulak.srms.dto;

/**
 * DTO representing a single course result row within a semester result.
 */
public class CourseResultDTO {

    private String courseCode;
    private String courseName;
    private Integer credits;
    private Double marksObtained;
    private String grade;
    private Double gradePoint;
    private Boolean isPassed;

    // ── Constructors ─────────────────────────────────────────────────────────

    public CourseResultDTO() {}

    public CourseResultDTO(String courseCode, String courseName, Integer credits,
                           Double marksObtained, String grade, Double gradePoint, Boolean isPassed) {
        this.courseCode = courseCode;
        this.courseName = courseName;
        this.credits = credits;
        this.marksObtained = marksObtained;
        this.grade = grade;
        this.gradePoint = gradePoint;
        this.isPassed = isPassed;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }

    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }

    public Integer getCredits() { return credits; }
    public void setCredits(Integer credits) { this.credits = credits; }

    public Double getMarksObtained() { return marksObtained; }
    public void setMarksObtained(Double marksObtained) { this.marksObtained = marksObtained; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Double getGradePoint() { return gradePoint; }
    public void setGradePoint(Double gradePoint) { this.gradePoint = gradePoint; }

    public Boolean getIsPassed() { return isPassed; }
    public void setIsPassed(Boolean isPassed) { this.isPassed = isPassed; }
}
