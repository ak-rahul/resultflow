package com.rahulak.srms.dto;

/**
 * DTO returned by the CGPA endpoint — contains the aggregated CGPA
 * computed via raw JDBC ({@link com.rahulak.srms.service.ResultService}).
 */
public class CgpaDTO {

    private Double cgpa;
    private Integer totalCourses;
    private Integer backlogs;

    // ── Constructors ─────────────────────────────────────────────────────────

    public CgpaDTO() {}

    public CgpaDTO(Double cgpa, Integer totalCourses, Integer backlogs) {
        this.cgpa = cgpa;
        this.totalCourses = totalCourses;
        this.backlogs = backlogs;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Double getCgpa() { return cgpa; }
    public void setCgpa(Double cgpa) { this.cgpa = cgpa; }

    public Integer getTotalCourses() { return totalCourses; }
    public void setTotalCourses(Integer totalCourses) { this.totalCourses = totalCourses; }

    public Integer getBacklogs() { return backlogs; }
    public void setBacklogs(Integer backlogs) { this.backlogs = backlogs; }
}
