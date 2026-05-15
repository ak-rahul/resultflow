package com.rahulak.srms.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO representing a student's result for one semester,
 * including per-semester GPA and a list of course results.
 */
public class SemesterResultDTO {

    private Integer semester;
    private String academicYear;
    private Double gpa;
    private Integer totalCredits;
    private List<CourseResultDTO> courses = new ArrayList<>();

    // ── Constructors ─────────────────────────────────────────────────────────

    public SemesterResultDTO() {}

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Integer getSemester() { return semester; }
    public void setSemester(Integer semester) { this.semester = semester; }

    public String getAcademicYear() { return academicYear; }
    public void setAcademicYear(String academicYear) { this.academicYear = academicYear; }

    public Double getGpa() { return gpa; }
    public void setGpa(Double gpa) { this.gpa = gpa; }

    public Integer getTotalCredits() { return totalCredits; }
    public void setTotalCredits(Integer totalCredits) { this.totalCredits = totalCredits; }

    public List<CourseResultDTO> getCourses() { return courses; }
    public void setCourses(List<CourseResultDTO> courses) { this.courses = courses; }
}
