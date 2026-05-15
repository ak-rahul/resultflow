package com.rahulak.srms.repository;

import com.rahulak.srms.model.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Spring Data JPA repository for {@link Enrollment}.
 */
@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Long> {

    /** Find all enrollments for a student (used for result calculation). */
    List<Enrollment> findByStudentId(Long studentId);

    /** Find enrollments for a student in a specific semester. */
    List<Enrollment> findByStudentIdAndSemester(Long studentId, Integer semester);

    /** Find enrollments for a student by roll number, ordered by semester. */
    List<Enrollment> findByStudent_RollNumberOrderBySemesterAsc(String rollNumber);

    /**
     * Duplicate enrollment check — used by {@link com.rahulak.srms.service.EnrollmentService}
     * before persisting a new enrollment.
     */
    boolean existsByStudentIdAndCourseIdAndSemesterAndAcademicYear(
            Long studentId, Long courseId, Integer semester, String academicYear);

    /** Find all enrollments for a student in a semester, eagerly fetching course. */
    List<Enrollment> findByStudent_RollNumberAndSemester(String rollNumber, Integer semester);
}
