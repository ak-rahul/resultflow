package com.rahulak.srms.repository;

import com.rahulak.srms.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Student}.
 *
 * <p>Spring auto-generates all CRUD methods at runtime.
 * Custom query methods below are derived from the method name by Spring Data.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    /** Find a student by their unique roll number. */
    Optional<Student> findByRollNumber(String rollNumber);

    /** Find all students in a given department. */
    List<Student> findByDepartment(String department);

    /** Case-insensitive partial name search (used by the search bar). */
    List<Student> findByNameContainingIgnoreCase(String name);

    /** Check existence before saving to prevent duplicate roll numbers. */
    boolean existsByRollNumber(String rollNumber);

    /** Find all active students. */
    List<Student> findByIsActiveTrue();

    /** Search by roll number OR name (case-insensitive). */
    List<Student> findByRollNumberContainingIgnoreCaseOrNameContainingIgnoreCase(String rollNumber, String name);
}
