package com.rahulak.srms.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

/**
 * Data Transfer Object for student create/update operations and API responses.
 *
 * <p>Decouples the API/MVC contract from the JPA entity,
 * allowing the entity schema to evolve independently.
 */
public class StudentDTO {

    private Long id;

    @NotBlank(message = "Roll number is required")
    @Size(max = 20, message = "Roll number must not exceed 20 characters")
    private String rollNumber;

    @NotBlank(message = "Name is required")
    @Size(max = 100, message = "Name must not exceed 100 characters")
    private String name;

    @Email(message = "Please provide a valid email address")
    private String email;

    private String phone;

    @NotBlank(message = "Department is required")
    private String department;

    @Min(value = 1, message = "Semester must be between 1 and 10")
    @Max(value = 10, message = "Semester must be between 1 and 10")
    private Integer currentSem = 1;

    private LocalDate dateOfBirth;

    private Boolean isActive = Boolean.TRUE;

    // ── Constructors ─────────────────────────────────────────────────────────

    public StudentDTO() {}

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
}
