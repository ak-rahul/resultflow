package com.rahulak.srms.repository;

import com.rahulak.srms.model.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for {@link Grade}.
 */
@Repository
public interface GradeRepository extends JpaRepository<Grade, Long> {

    /** Find the grade for a specific enrollment (one-to-one). */
    Optional<Grade> findByEnrollmentId(Long enrollmentId);

    /** Find all grades for a student by roll number (traverses enrollment → student). */
    List<Grade> findByEnrollment_Student_RollNumber(String rollNumber);

    /** Find all grades for a student in a specific semester. */
    List<Grade> findByEnrollment_Student_RollNumberAndEnrollment_Semester(String rollNumber, Integer semester);

    /** Check if a grade already exists for an enrollment. */
    boolean existsByEnrollmentId(Long enrollmentId);
}
