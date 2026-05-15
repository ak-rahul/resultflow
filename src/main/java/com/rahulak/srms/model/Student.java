package com.rahulak.srms.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * JPA entity representing a student.
 *
 * <p>Maps to the {@code students} table. Each student can be enrolled in many
 * courses across multiple semesters (via the {@code enrollments} table).
 */
@Entity
@Table(name = "students")
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "roll_number", nullable = false, unique = true, length = 20)
    @NotBlank(message = "Roll number is required")
    @Size(max = 20, message = "Roll number must not exceed 20 characters")
    private String rollNumber;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Column(name = "email", unique = true, length = 100)
    @Email(message = "Please provide a valid email address")
    private String email;

    @Column(name = "phone", length = 15)
    private String phone;

    @Column(name = "department", nullable = false, length = 100)
    @NotBlank(message = "Department is required")
    private String department;

    @Column(name = "current_sem", nullable = false)
    @Min(value = 1, message = "Semester must be at least 1")
    @Max(value = 10, message = "Semester must not exceed 10")
    private Integer currentSem = 1;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = Boolean.TRUE;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Enrollment> enrollments = new ArrayList<>();

    // ── Lifecycle callbacks ──────────────────────────────────────────────────

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // ── Constructors ─────────────────────────────────────────────────────────

    public Student() {}

    public Student(String rollNumber, String name, String email, String department, Integer currentSem) {
        this.rollNumber = rollNumber;
        this.name = name;
        this.email = email;
        this.department = department;
        this.currentSem = currentSem;
    }

    // ── Getters & Setters ────────────────────────────────────────────────────

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRollNumber() { return rollNumber; }
    public void setRollNumber(String rollNumber) { this.rollNumber = rollNumber; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getCurrentSem() { return currentSem; }
    public void setCurrentSem(Integer currentSem) { this.currentSem = currentSem; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public List<Enrollment> getEnrollments() { return enrollments; }
    public void setEnrollments(List<Enrollment> enrollments) { this.enrollments = enrollments; }

    @Override
    public String toString() {
        return "Student{id=" + id + ", rollNumber='" + rollNumber + "', name='" + name + "'}";
    }
}
