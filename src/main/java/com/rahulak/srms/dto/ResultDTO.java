package com.rahulak.srms.dto;

import java.util.ArrayList;
import java.util.List;

/**
 * Full result DTO for a student — top-level response for the transcript endpoint.
 *
 * <p>Contains the student's identity info, aggregated CGPA data (from raw JDBC),
 * and a semester-by-semester breakdown of course results.
 */
public class ResultDTO {

    private String rollNumber;
    private String name;
    private String department;

    // ── From CGPA JDBC aggregation ───────────────────────────────────────────
    private Double cgpa;
    private Integer totalCourses;
    private Integer backlogs;

    // ── Per-semester breakdown ───────────────────────────────────────────────
    private List<SemesterResultDTO> semesters = new ArrayList<>();

    // ── Constructors ─────────────────────────────────────────────────────────

    public ResultDTO() {}

    // ── Getters & Setters ────────────────────────────────────────────────────

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Integer getTotalCourses() { return totalCourses; }
    public void setTotalCourses(Integer totalCourses) { this.totalCourses = totalCourses; }

    public Integer getBacklogs() { return backlogs; }
    public void setBacklogs(Integer backlogs) { this.backlogs = backlogs; }

    public List<SemesterResultDTO> getSemesters() { return semesters; }
    public void setSemesters(List<SemesterResultDTO> semesters) { this.semesters = semesters; }
}
